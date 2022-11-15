package io.github.portlek.smol.tasks

import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.google.gson.GsonBuilder
import io.github.portlek.smol.SHADOW_ID
import io.github.portlek.smol.SMOL_JAR_TASK_NAME
import io.github.portlek.smol.SmolPlugin
import io.github.portlek.smol.func.performCompileTimeResolution
import io.github.portlek.smol.func.smolInjectToIsolated
import io.github.portlek.smol.relocation.RelocationConfig
import io.github.portlek.smol.relocation.RelocationRule
import io.github.portlek.smol.resolver.CachingDependencyResolver
import io.github.portlek.smol.resolver.ResolutionResult
import io.github.portlek.smol.resolver.data.Dependency
import io.github.portlek.smol.resolver.data.DependencyData
import io.github.portlek.smol.resolver.data.Mirror
import io.github.portlek.smol.resolver.data.Repository
import io.github.portlek.smol.resolver.enquirer.PingingRepositoryEnquirerFactory
import io.github.portlek.smol.resolver.mirrors.SimpleMirrorSelector
import io.github.portlek.smol.resolver.pinger.HttpURLPinger
import io.github.portlek.smol.resolver.reader.facade.TypeToken
import io.github.portlek.smol.resolver.strategy.MavenChecksumPathResolutionStrategy
import io.github.portlek.smol.resolver.strategy.MavenPathResolutionStrategy
import io.github.portlek.smol.resolver.strategy.MavenPomPathResolutionStrategy
import io.github.portlek.smol.resolver.strategy.MavenSnapshotPathResolutionStrategy
import io.github.portlek.smol.resolver.strategy.MediatingPathResolutionStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.diagnostics.internal.graph.nodes.RenderableDependency
import org.gradle.api.tasks.diagnostics.internal.graph.nodes.RenderableModuleResult
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.reflect.Type
import java.net.URL
import javax.inject.Inject

private val scope = CoroutineScope(Dispatchers.IO)

@CacheableTask
abstract class SmolJar
@Inject
constructor(private val smolConfig: Configuration, private val smolApiConfig: Configuration) :
  DefaultTask() {

  private val relocations = mutableSetOf<RelocationRule>()
  internal fun relocations() = relocations
  private fun addRelocation(
    original: String,
    relocated: String,
    configure: Action<RelocationConfig>? = null
  ): SmolJar {
    val relocationConfig = RelocationConfig()
    configure?.execute(relocationConfig)
    relocations.add(
      RelocationRule(
        original, relocated, relocationConfig.exclusions, relocationConfig.inclusions
      )
    )
    return this
  }

  private val mirrors = mutableSetOf<Mirror>()
  private val isolatedProjects = mutableSetOf<Project>()

  private val gson = GsonBuilder().create()
  private val shadowWriteFolder = File("${project.buildDir}/resources/main/")

  val outputDirectory = File("${project.buildDir}/resources/smol/")
    @OutputDirectory get

  init {
    group = "smolJar"
    inputs.files(smolConfig)
  }

  open fun relocate(original: String, relocated: String) = addRelocation(original, relocated, null)

  open fun relocate(original: String, relocated: String, configure: Action<RelocationConfig>) =
    addRelocation(original, relocated, configure)

  open fun mirror(mirror: String, original: String) = mirrors.add(Mirror(URL(mirror), URL(original)))

  open infix fun String.mirroring(original: String) {
    mirrors.add(Mirror(URL(this), URL(original)))
  }

  open fun isolate(proj: Project) {
    isolatedProjects.add(proj)
    if (proj.smolInjectToIsolated) {
      proj.pluginManager.apply(ShadowPlugin::class.java)
      proj.pluginManager.apply(SmolPlugin::class.java)
    }
    dependsOn(SHADOW_ID)
  }

  @TaskAction
  internal fun createJson() = with(project) {
    val dependencies =
      RenderableModuleResult(smolConfig.incoming.resolutionResult.root)
        .children
        .mapNotNull { it.toSmolDependency() }
        .toMutableSet()
    dependencies.addAll(
      RenderableModuleResult(smolApiConfig.incoming.resolutionResult.root)
        .children
        .mapNotNull { it.toSmolDependency() }
    )
    val repositories = repositories.filterIsInstance<MavenArtifactRepository>()
      .filterNot { it.url.toString().startsWith("file") }
      .toSet()
      .map { Repository(it.url.toURL()) }
    if (outputDirectory.exists().not()) {
      outputDirectory.mkdirs()
    }
    val file = File(outputDirectory, "smol.json")
    FileWriter(file).use {
      gson.toJson(DependencyData(mirrors, repositories, dependencies, relocations), it)
    }
    if (shadowWriteFolder.exists().not()) shadowWriteFolder.mkdirs()
    file.copyTo(File(shadowWriteFolder, file.name), true)
  }

  @TaskAction
  internal fun includeIsolatedJars() = with(project) {
    isolatedProjects
      .filter { it != this }
      .forEach {
        val shadowTask = it.getTasksByName("shadowJar", true).first()
        val archive = shadowTask.outputs.files.singleFile
        if (outputDirectory.exists().not()) {
          outputDirectory.mkdirs()
        }
        val output = File(outputDirectory, "${it.name}.isolated-jar")
        archive.copyTo(output, true)
        if (shadowWriteFolder.exists().not()) {
          shadowWriteFolder.mkdirs()
        }
        output.copyTo(File(shadowWriteFolder, output.name), true)
      }
  }

  @TaskAction
  internal fun generateResolvedDependenciesFile() = with(project) {
    if (project.performCompileTimeResolution.not()) {
      return@with
    }
    fun Collection<Dependency>.flatten(): MutableSet<Dependency> =
      flatMap { it.transitive().flatten() + it }.toMutableSet()
    val file = File(outputDirectory, "smol-resolutions.json")
    val mapType = TypeToken<MutableMap<String, ResolutionResult>>().rawType()
    val preResolved = if (file.exists()) {
      gson.fromJson(FileReader(file), mapType)
    } else {
      mutableMapOf<String, ResolutionResult>()
    }
    val dependencies = RenderableModuleResult(smolConfig.incoming.resolutionResult.root)
      .children
      .mapNotNull { it.toSmolDependency() }
      .toMutableSet()
      .flatten()
    val repositories = repositories.filterIsInstance<MavenArtifactRepository>()
      .filterNot { it.url.toString().startsWith("file") }
      .toSet()
      .map { Repository(it.url.toURL()) }
    val releaseStrategy = MavenPathResolutionStrategy()
    val snapshotStrategy = MavenSnapshotPathResolutionStrategy()
    val resolutionStrategy = MediatingPathResolutionStrategy(releaseStrategy, snapshotStrategy)
    val pomURLCreationStrategy = MavenPomPathResolutionStrategy()
    val checksumResolutionStrategy = MavenChecksumPathResolutionStrategy("SHA-1", resolutionStrategy)
    val urlPinger = HttpURLPinger()
    val enquirerFactory = PingingRepositoryEnquirerFactory(
      resolutionStrategy,
      checksumResolutionStrategy,
      pomURLCreationStrategy,
      urlPinger
    )
    val mirrorSelector = SimpleMirrorSelector()
    val resolver = CachingDependencyResolver(
      urlPinger,
      mirrorSelector.select(repositories, mirrors),
      enquirerFactory,
      mapOf()
    )
    val result = runBlocking(Dispatchers.IO) {
      dependencies
        .filter {
          preResolved[it.toString()]?.let { pre ->
            repositories.none { r ->
              pre.repository()?.url().toString() == r.url().toString()
            }
          } ?: true
        }
        .map {
          scope.async { it.toString() to resolver.resolve(it).orElse(null) }
        }
        .associate { it.await() }
        .filterValues { it != null }
        .toMutableMap()
    }
    preResolved.forEach {
      result.putIfAbsent(it.key, it.value)
    }
    if (outputDirectory.exists().not()) {
      outputDirectory.mkdirs()
    }
    FileWriter(file).use {
      gson.toJson(result, it)
    }
    if (shadowWriteFolder.exists().not()) {
      shadowWriteFolder.mkdirs()
    }
    file.copyTo(File(shadowWriteFolder, file.name), true)
  }

  private fun RenderableDependency.toSmolDependency(): Dependency? {
    val transitive = mutableSetOf<Dependency>()
    collectTransitive(transitive, children)
    return id.toString().toDependency(transitive)
  }

  private fun collectTransitive(
    transitive: MutableSet<Dependency>,
    dependencies: Set<RenderableDependency>
  ) {
    for (dependency in dependencies) {
      val dep = dependency.id.toString().toDependency(emptySet()) ?: continue
      if (dep in transitive) {
        continue
      }
      transitive.add(dep)
      collectTransitive(transitive, dependency.children)
    }
  }

  private fun String.toDependency(transitive: Set<Dependency>): Dependency? {
    val values = split(":")
    val group = values.getOrNull(0) ?: return null
    val artifact = values.getOrNull(1) ?: return null
    val version = values.getOrNull(2) ?: return null
    val snapshot = values.getOrNull(3)
    return Dependency(group, artifact, version, snapshot, transitive)
  }
}
