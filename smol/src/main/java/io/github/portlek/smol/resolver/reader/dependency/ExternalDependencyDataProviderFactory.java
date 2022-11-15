package io.github.portlek.smol.resolver.reader.dependency;

import io.github.portlek.smol.resolver.reader.facade.GsonFacade;
import io.github.portlek.smol.resolver.reader.facade.GsonFacadeFactory;
import java.net.URL;
import org.jetbrains.annotations.NotNull;

public record ExternalDependencyDataProviderFactory(@NotNull GsonFacade gson)
  implements DependencyDataProviderFactory {
  public ExternalDependencyDataProviderFactory(
    @NotNull final GsonFacadeFactory gsonFactory
  ) throws ReflectiveOperationException {
    this(gsonFactory.createFacade());
  }

  @NotNull
  @Override
  public DependencyDataProvider create(@NotNull final URL dependencyFileURL) {
    return new ModuleDependencyDataProvider(
      new GsonDependencyReader(this.gson),
      dependencyFileURL
    );
  }
}
