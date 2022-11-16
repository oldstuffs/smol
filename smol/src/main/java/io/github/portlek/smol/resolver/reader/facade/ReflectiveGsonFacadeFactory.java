package io.github.portlek.smol.resolver.reader.facade;

import io.github.portlek.smol.app.builder.ApplicationBuilder;
import io.github.portlek.smol.injector.loader.InjectableClassLoader;
import io.github.portlek.smol.injector.loader.IsolatedInjectableClassLoader;
import io.github.portlek.smol.misc.Packages;
import io.github.portlek.smol.relocation.PassthroughRelocator;
import io.github.portlek.smol.resolver.data.Dependency;
import io.github.portlek.smol.resolver.data.DependencyData;
import io.github.portlek.smol.resolver.data.Repository;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;

public record ReflectiveGsonFacadeFactory(
  @NotNull Constructor<?> gsonConstructor,
  @NotNull Method gsonFromJsonMethod,
  @NotNull Method gsonFromJsonTypeMethod,
  @NotNull Method canonicalizeMethod
)
  implements GsonFacadeFactory {
  private static final String GSON_PACKAGE = "com#google#gson#Gson";

  private static final String GSON_TYPES_PACKAGE =
    "com#google#gson#internal#$Gson$Types";

  @NotNull
  public static GsonFacadeFactory create(
    @NotNull final Path downloadPath,
    @NotNull final Collection<Repository> repositories
  )
    throws ReflectiveOperationException, NoSuchAlgorithmException, IOException, URISyntaxException {
    final var classLoader = new IsolatedInjectableClassLoader();
    return ReflectiveGsonFacadeFactory.create(
      downloadPath,
      repositories,
      classLoader
    );
  }

  @NotNull
  public static GsonFacadeFactory create(
    @NotNull final Path downloadPath,
    @NotNull final Collection<Repository> repositories,
    @NotNull final InjectableClassLoader classLoader
  )
    throws ReflectiveOperationException, NoSuchAlgorithmException, IOException, URISyntaxException {
    ApplicationBuilder
      .injecting("Smol", classLoader)
      .downloadDirectoryPath(downloadPath)
      .dataProviderFactory(url ->
        () -> ReflectiveGsonFacadeFactory.getGsonDependency(repositories)
      )
      .relocatorFactory(rules -> new PassthroughRelocator())
      .preResolutionDataProviderFactory(a -> Collections::emptyMap)
      .relocationHelperFactory(r -> (dependency, file) -> file)
      .build();
    final Class<?> gsonClass = Class.forName(
      Packages.fix(ReflectiveGsonFacadeFactory.GSON_PACKAGE),
      true,
      classLoader
    );
    final Constructor<?> gsonConstructor = gsonClass.getConstructor();
    final Method gsonFromJsonMethod = gsonClass.getMethod(
      "fromJson",
      Reader.class,
      Class.class
    );
    final Method gsonFromJsonTypeMethod = gsonClass.getMethod(
      "fromJson",
      Reader.class,
      Type.class
    );
    final Class<?> gsonTypesClass = Class.forName(
      Packages.fix(ReflectiveGsonFacadeFactory.GSON_TYPES_PACKAGE),
      true,
      classLoader
    );
    final Method canonicalizeMethod = gsonTypesClass.getMethod(
      "canonicalize",
      Type.class
    );
    return new ReflectiveGsonFacadeFactory(
      gsonConstructor,
      gsonFromJsonMethod,
      gsonFromJsonTypeMethod,
      canonicalizeMethod
    );
  }

  @NotNull
  private static DependencyData getGsonDependency(
    @NotNull final Collection<Repository> repositories
  ) {
    final var gson = new Dependency(
      Packages.fix("com#google#code#gson"),
      "gson",
      "2.10",
      null,
      new HashSet<>()
    );
    return new DependencyData(
      Collections.emptySet(),
      repositories,
      Collections.singleton(gson),
      Collections.emptyList()
    );
  }

  @Override
  public GsonFacade createFacade() throws ReflectiveOperationException {
    return new ReflectiveGsonFacade(
      this.gsonConstructor.newInstance(),
      this.gsonFromJsonMethod,
      this.gsonFromJsonTypeMethod,
      this.canonicalizeMethod
    );
  }
}
