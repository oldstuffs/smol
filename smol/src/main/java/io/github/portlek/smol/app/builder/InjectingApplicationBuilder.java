package io.github.portlek.smol.app.builder;

import io.github.portlek.smol.app.AppendingApplication;
import io.github.portlek.smol.app.Application;
import io.github.portlek.smol.injector.loader.Injectable;
import io.github.portlek.smol.injector.loader.InjectableFactory;
import io.github.portlek.smol.resolver.data.Repository;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class InjectingApplicationBuilder extends ApplicationBuilder {

  @NotNull
  Function<ApplicationBuilder, Injectable> injectableSupplier;

  public InjectingApplicationBuilder(
    @NotNull final String applicationName,
    @NotNull final Injectable injectable
  ) {
    this(applicationName, it -> injectable);
  }

  public InjectingApplicationBuilder(
    @NotNull final String applicationName,
    @NotNull final Function<ApplicationBuilder, Injectable> injectableSupplier
  ) {
    super(applicationName);
    this.injectableSupplier = injectableSupplier;
  }

  @NotNull
  public static ApplicationBuilder createAppending(
    @NotNull final String applicationName
  ) {
    final var classLoader = ApplicationBuilder.class.getClassLoader();
    return InjectingApplicationBuilder.createAppending(
      applicationName,
      classLoader
    );
  }

  @NotNull
  public static ApplicationBuilder createAppending(
    @NotNull final String applicationName,
    @NotNull final ClassLoader classLoader
  ) {
    return new InjectingApplicationBuilder(
      applicationName,
      builder -> {
        try {
          return InjectableFactory.create(
            builder.getDownloadDirectoryPath(),
            Collections.singleton(Repository.central()),
            classLoader
          );
        } catch (
          final URISyntaxException
          | ReflectiveOperationException
          | NoSuchAlgorithmException
          | IOException exception
        ) {
          exception.printStackTrace();
        }
        return null;
      }
    );
  }

  @NotNull
  @Override
  public Application buildApplication()
    throws IOException, ReflectiveOperationException, URISyntaxException, NoSuchAlgorithmException {
    final var dataProvider =
      this.getDataProviderFactory().create(this.getDependencyFileUrl());
    final var dependencyData = dataProvider.get();
    final var dependencyInjector = this.createInjector();
    final var preResolutionDataProvider =
      this.getPreResolutionDataProviderFactory()
        .create(this.getPreResolutionFileUrl());
    final var preResolutionResultMap = preResolutionDataProvider.get();
    dependencyInjector.inject(
      this.injectableSupplier.apply(this),
      dependencyData,
      preResolutionResultMap
    );
    return new AppendingApplication();
  }
}
