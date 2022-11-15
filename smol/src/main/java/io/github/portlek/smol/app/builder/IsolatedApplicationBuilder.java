package io.github.portlek.smol.app.builder;

import io.github.portlek.smol.app.Application;
import io.github.portlek.smol.injector.loader.IsolatedInjectableClassLoader;
import io.github.portlek.smol.misc.Modules;
import io.github.portlek.smol.misc.Parameters;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class IsolatedApplicationBuilder extends ApplicationBuilder {

  @NotNull
  Object@NotNull[] arguments;

  @NotNull
  IsolationConfiguration isolationConfiguration;

  public IsolatedApplicationBuilder(
    @NotNull final String applicationName,
    @NotNull final IsolationConfiguration isolationConfiguration,
    @NotNull final Object@NotNull[] arguments
  ) {
    super(applicationName);
    this.isolationConfiguration = isolationConfiguration;
    this.arguments = arguments.clone();
  }

  @NotNull
  @Override
  public Application buildApplication()
    throws IOException, ReflectiveOperationException, URISyntaxException, NoSuchAlgorithmException {
    final var injector = this.createInjector();
    final var moduleUrls = Modules.extract(
      this.isolationConfiguration.moduleExtractor(),
      this.isolationConfiguration.modules()
    );
    final var classLoader = new IsolatedInjectableClassLoader(
      moduleUrls,
      this.isolationConfiguration.parentClassloader(),
      Collections.singleton(Application.class)
    );
    final var dataProvider =
      this.getDataProviderFactory().create(this.getDependencyFileUrl());
    final var selfDependencyData = dataProvider.get();
    final var preResolutionDataProvider =
      this.getPreResolutionDataProviderFactory()
        .create(this.getPreResolutionFileUrl());
    final var preResolutionResultMap = preResolutionDataProvider.get();
    injector.inject(classLoader, selfDependencyData, preResolutionResultMap);
    for (final var module : moduleUrls) {
      injector.inject(
        classLoader,
        this.getModuleDataProviderFactory().create(module).get(),
        preResolutionResultMap
      );
    }
    final var applicationClass = (Class<Application>) Class.forName(
      this.isolationConfiguration.applicationClass(),
      true,
      classLoader
    );
    return applicationClass
      .getConstructor(Parameters.typesFrom(this.arguments))
      .newInstance(this.arguments);
  }
}
