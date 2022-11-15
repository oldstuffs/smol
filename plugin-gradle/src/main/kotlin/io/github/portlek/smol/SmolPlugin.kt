package io.github.portlek.smol

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.github.portlek.smol.exceptions.ShadowNotFoundException
import io.github.portlek.smol.func.createConfig
import io.github.portlek.smol.tasks.SmolJar
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.extra

const val SMOL_CONFIGURATION_NAME = "smol"
const val SMOL_API_CONFIGURATION_NAME = "smolApi"
const val SMOL_JAR_TASK_NAME = "smolJar"
const val SHADOW_ID = "com.github.johnrengelman.shadow"
const val RESOURCES_TASK = "processResources"

class SmolPlugin : Plugin<Project> {

  override fun apply(target: Project): Unit =
      with(target) {
        plugins.apply(JavaPlugin::class.java)
        plugins.apply(JavaLibraryPlugin::class.java)
        if (!plugins.hasPlugin(SHADOW_ID)) {
          throw ShadowNotFoundException(
              "Smol depends on the Shadow plugin, please apply the plugin. For more information visit: https://imperceptiblethoughts.com/shadow/")
        }
        val smolConfig =
            createConfig(
                SMOL_CONFIGURATION_NAME,
                JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME,
                JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME)
        val smolApiConfig =
            createConfig(
                SMOL_API_CONFIGURATION_NAME,
                JavaPlugin.COMPILE_ONLY_API_CONFIGURATION_NAME,
                JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME)
        val smolJar =
            tasks.create(SMOL_JAR_TASK_NAME, SmolJar::class.java, smolConfig, smolApiConfig)
        extra.set("smol", asGroovyClosure("+") { version -> smolJarLib(version) })
        val shadowTask = tasks.withType(ShadowJar::class.java).first()
        shadowTask.doFirst {
          smolJar.relocations().forEach { rule ->
            shadowTask.relocate(rule.originalPackagePattern(), rule.relocatedPackagePattern()) {
              rule.inclusions().forEach { include(it) }
              rule.exclusions().forEach { exclude(it) }
            }
          }
        }
        tasks.findByName(RESOURCES_TASK)?.finalizedBy(smolJar)
      }
}

internal fun smolJarLib(version: String) = "io.github.portlek:smol:$version"
