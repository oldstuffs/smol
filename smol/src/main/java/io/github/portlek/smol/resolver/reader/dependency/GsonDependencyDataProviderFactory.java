package io.github.portlek.smol.resolver.reader.dependency;

import io.github.portlek.smol.resolver.reader.facade.GsonFacade;
import io.github.portlek.smol.resolver.reader.facade.GsonFacadeFactory;
import java.net.URL;
import org.jetbrains.annotations.NotNull;

public record GsonDependencyDataProviderFactory(@NotNull GsonFacade gson)
  implements DependencyDataProviderFactory {
  public GsonDependencyDataProviderFactory(
    @NotNull final GsonFacadeFactory gsonFactory
  ) throws ReflectiveOperationException {
    this(gsonFactory.createFacade());
  }

  @NotNull
  @Override
  public DependencyDataProvider create(@NotNull final URL dependencyFileURL) {
    return new URLDependencyDataProvider(
      dependencyFileURL,
      new GsonDependencyReader(this.gson)
    );
  }
}
