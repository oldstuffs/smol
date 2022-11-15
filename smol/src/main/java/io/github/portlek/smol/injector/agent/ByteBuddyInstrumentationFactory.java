package io.github.portlek.smol.injector.agent;

import io.github.portlek.smol.app.builder.ApplicationBuilder;
import io.github.portlek.smol.app.module.ModuleExtractor;
import io.github.portlek.smol.app.module.TemporaryModuleExtractor;
import io.github.portlek.smol.injector.loader.InstrumentationInjectable;
import io.github.portlek.smol.injector.loader.IsolatedInjectableClassLoader;
import io.github.portlek.smol.injector.loader.manifest.JarManifestGenerator;
import io.github.portlek.smol.misc.Packages;
import io.github.portlek.smol.relocation.JarFileRelocator;
import io.github.portlek.smol.relocation.PassthroughRelocator;
import io.github.portlek.smol.relocation.RelocationRule;
import io.github.portlek.smol.relocation.facade.JarRelocatorFacadeFactory;
import io.github.portlek.smol.resolver.data.Dependency;
import io.github.portlek.smol.resolver.data.DependencyData;
import io.github.portlek.smol.resolver.data.Repository;
import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ByteBuddyInstrumentationFactory(
  @NotNull JarRelocatorFacadeFactory relocatorFacadeFactory,
  @Nullable URL agentJarUrl,
  @NotNull ModuleExtractor extractor
)
  implements InstrumentationFactory {
  private static final String AGENT_CLASS = "ClassLoaderAgent";

  private static final String AGENT_JAR = "loader-agent.isolated-jar";

  private static final String AGENT_PACKAGE =
    "io#github#portlek##smol#injector#agent";

  private static final String BYTE_BUDDY_AGENT_CLASS =
    "net#bytebuddy#agent#ByteBuddyAgent";

  public ByteBuddyInstrumentationFactory(
    @NotNull final JarRelocatorFacadeFactory relocatorFacadeFactory
  ) {
    this(
      relocatorFacadeFactory,
      InstrumentationInjectable.class.getClassLoader()
        .getResource(ByteBuddyInstrumentationFactory.AGENT_JAR),
      new TemporaryModuleExtractor()
    );
  }

  @NotNull
  private static String generatePattern() {
    return String.format("smol.%s", UUID.randomUUID());
  }

  @NotNull
  private static DependencyData getDependency() throws MalformedURLException {
    final var byteBuddy = new Dependency(
      "net.bytebuddy",
      "byte-buddy-agent",
      "1.12.18",
      null,
      Collections.emptyList()
    );
    return new DependencyData(
      Collections.emptySet(),
      Collections.singleton(Repository.central()),
      Collections.singleton(byteBuddy),
      Collections.emptyList()
    );
  }

  @NotNull
  @Override
  public Instrumentation create()
    throws IOException, ReflectiveOperationException, URISyntaxException, NoSuchAlgorithmException {
    final var extractedURL =
      this.extractor.extractModule(
          Objects.requireNonNull(this.agentJarUrl, "agent jar url"),
          "loader-agent"
        );
    final var pattern = ByteBuddyInstrumentationFactory.generatePattern();
    final var relocatedAgentClass = String.format(
      "%s.%s",
      pattern,
      ByteBuddyInstrumentationFactory.AGENT_CLASS
    );
    final var relocationRule = new RelocationRule(
      Packages.fix(ByteBuddyInstrumentationFactory.AGENT_PACKAGE),
      pattern,
      Collections.emptySet(),
      Collections.emptySet()
    );
    final var relocator = new JarFileRelocator(
      Collections.singleton(relocationRule),
      this.relocatorFacadeFactory
    );
    final var inputFile = new File(extractedURL.toURI());
    final var relocatedFile = File.createTempFile("smol-agent", ".jar");
    final var classLoader = new IsolatedInjectableClassLoader();
    relocator.relocate(inputFile, relocatedFile);
    JarManifestGenerator
      .with(relocatedFile.toURI())
      .attribute("Manifest-Version", "1.0")
      .attribute("Agent-Class", relocatedAgentClass)
      .generate();
    ApplicationBuilder
      .injecting("Smol-Agent", classLoader)
      .dataProviderFactory(dataUrl ->
        ByteBuddyInstrumentationFactory::getDependency
      )
      .relocatorFactory(rules -> new PassthroughRelocator())
      .relocationHelperFactory(rel -> (dependency, file) -> file)
      .build();
    final var byteBuddyAgentClass = Class.forName(
      Packages.fix(ByteBuddyInstrumentationFactory.BYTE_BUDDY_AGENT_CLASS),
      true,
      classLoader
    );
    final var attachMethod = byteBuddyAgentClass.getMethod(
      "attach",
      File.class,
      String.class,
      String.class
    );
    final var processHandle = Class.forName("java.lang.ProcessHandle");
    final var currentMethod = processHandle.getMethod("current");
    final var pidMethod = processHandle.getMethod("pid");
    final var currentProcess = currentMethod.invoke(processHandle);
    final var processId = (Long) pidMethod.invoke(currentProcess);
    attachMethod.invoke(null, relocatedFile, String.valueOf(processId), "");
    final var agentClass = Class.forName(
      relocatedAgentClass,
      true,
      ClassLoader.getSystemClassLoader()
    );
    final var instrMethod = agentClass.getMethod("getInstrumentation");
    return (Instrumentation) instrMethod.invoke(null);
  }
}
