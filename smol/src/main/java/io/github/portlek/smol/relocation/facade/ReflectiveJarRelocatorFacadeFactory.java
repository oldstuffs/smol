package io.github.portlek.smol.relocation.facade;

import io.github.portlek.smol.app.builder.ApplicationBuilder;
import io.github.portlek.smol.injector.loader.InjectableClassLoader;
import io.github.portlek.smol.injector.loader.IsolatedInjectableClassLoader;
import io.github.portlek.smol.misc.Packages;
import io.github.portlek.smol.relocation.PassthroughRelocator;
import io.github.portlek.smol.relocation.RelocationRule;
import io.github.portlek.smol.resolver.data.Dependency;
import io.github.portlek.smol.resolver.data.DependencyData;
import io.github.portlek.smol.resolver.data.Repository;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;

public record ReflectiveJarRelocatorFacadeFactory(
  @NotNull Constructor<?> jarRelocatorConstructor,
  @NotNull Constructor<?> relocationConstructor,
  @NotNull Method jarRelocatorRunMethod
)
  implements JarRelocatorFacadeFactory {
  private static final String JAR_RELOCATOR_PACKAGE =
    "me#lucko#jarrelocator#JarRelocator";

  private static final String RELOCATION_PACKAGE =
    "me#lucko#jarrelocator#Relocation";

  @NotNull
  public static JarRelocatorFacadeFactory create(
    @NotNull final Path downloadPath,
    @NotNull final Collection<Repository> repositories
  )
    throws URISyntaxException, ReflectiveOperationException, NoSuchAlgorithmException, IOException {
    final var classLoader = new IsolatedInjectableClassLoader();
    return ReflectiveJarRelocatorFacadeFactory.create(
      downloadPath,
      repositories,
      classLoader
    );
  }

  @NotNull
  public static JarRelocatorFacadeFactory create(
    @NotNull final Path downloadPath,
    @NotNull final Collection<Repository> repositories,
    @NotNull final InjectableClassLoader classLoader
  )
    throws URISyntaxException, ReflectiveOperationException, NoSuchAlgorithmException, IOException {
    ApplicationBuilder
      .injecting("Smol", classLoader)
      .downloadDirectoryPath(downloadPath)
      .preResolutionDataProviderFactory(a -> Collections::emptyMap)
      .dataProviderFactory(url ->
        () ->
          ReflectiveJarRelocatorFacadeFactory.getJarRelocatorDependency(
            repositories
          )
      )
      .relocatorFactory(rules -> new PassthroughRelocator())
      .relocationHelperFactory(relocator -> (dependency, file) -> file)
      .build();
    final var jarRelocatorClass = Class.forName(
      Packages.fix(ReflectiveJarRelocatorFacadeFactory.JAR_RELOCATOR_PACKAGE),
      true,
      classLoader
    );
    final var relocationClass = Class.forName(
      Packages.fix(ReflectiveJarRelocatorFacadeFactory.RELOCATION_PACKAGE),
      true,
      classLoader
    );
    final var jarRelocatorConstructor = jarRelocatorClass.getConstructor(
      File.class,
      File.class,
      Collection.class
    );
    final var relocationConstructor = relocationClass.getConstructor(
      String.class,
      String.class,
      Collection.class,
      Collection.class
    );
    final var runMethod = jarRelocatorClass.getMethod("run");
    return new ReflectiveJarRelocatorFacadeFactory(
      jarRelocatorConstructor,
      relocationConstructor,
      runMethod
    );
  }

  @NotNull
  private static Object createRelocation(
    @NotNull final Constructor<?> relocationConstructor,
    @NotNull final RelocationRule rule
  )
    throws IllegalAccessException, InvocationTargetException, InstantiationException {
    return relocationConstructor.newInstance(
      rule.originalPackagePattern(),
      rule.relocatedPackagePattern(),
      rule.exclusions(),
      rule.inclusions()
    );
  }

  @NotNull
  private static Object createRelocator(
    @NotNull final Constructor<?> jarRelocatorConstructor,
    @NotNull final File input,
    @NotNull final File output,
    @NotNull final Collection<Object> rules
  )
    throws IllegalAccessException, InvocationTargetException, InstantiationException {
    return jarRelocatorConstructor.newInstance(input, output, rules);
  }

  @NotNull
  private static DependencyData getJarRelocatorDependency(
    @NotNull final Collection<Repository> repositories
  ) {
    final var asm = new Dependency(
      Packages.fix("org#ow2#asm"),
      "asm",
      "9.4",
      null,
      Collections.emptyList()
    );
    final var asmCommons = new Dependency(
      Packages.fix("org#ow2#asm"),
      "asm-commons",
      "9.4",
      null,
      Collections.emptyList()
    );
    final var jarRelocator = new Dependency(
      Packages.fix("me#lucko"),
      "jar-relocator",
      "1.5",
      null,
      Arrays.asList(asm, asmCommons)
    );
    return new DependencyData(
      Collections.emptySet(),
      repositories,
      Collections.singleton(jarRelocator),
      Collections.emptyList()
    );
  }

  @NotNull
  @Override
  public JarRelocatorFacade createFacade(
    @NotNull final File input,
    @NotNull final File output,
    @NotNull final Collection<RelocationRule> relocationRules
  )
    throws IllegalAccessException, InstantiationException, InvocationTargetException {
    final var relocations = new HashSet<>();
    for (final var rule : relocationRules) {
      relocations.add(
        ReflectiveJarRelocatorFacadeFactory.createRelocation(
          this.relocationConstructor,
          rule
        )
      );
    }
    final var relocator = ReflectiveJarRelocatorFacadeFactory.createRelocator(
      this.jarRelocatorConstructor,
      input,
      output,
      relocations
    );
    return new ReflectiveJarRelocatorFacade(
      relocator,
      this.jarRelocatorRunMethod
    );
  }
}
