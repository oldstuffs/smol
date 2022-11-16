package io.github.portlek.smol.injector;

import io.github.portlek.smol.downloader.DownloadNotifier;
import io.github.portlek.smol.injector.helper.InjectionHelperFactory;
import org.jetbrains.annotations.NotNull;

public record SimpleDependencyInjectorFactory(
  @NotNull DownloadNotifier downloadNotifier
)
  implements DependencyInjectorFactory {
  @NotNull
  @Override
  public DependencyInjector create(
    @NotNull final InjectionHelperFactory injectionHelperFactory
  ) {
    return new SimpleDependencyInjector(
      injectionHelperFactory,
      this.downloadNotifier
    );
  }
}
