package io.github.portlek.smol.injector.helper;

import io.github.portlek.smol.downloader.DependencyDownloader;
import io.github.portlek.smol.relocation.helper.RelocationHelper;
import io.github.portlek.smol.resolver.data.Dependency;
import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record InjectionHelper(
  @NotNull DependencyDownloader dependencyDownloader,
  @NotNull RelocationHelper relocationHelper
) {
  @Nullable
  public File fetch(@NotNull final Dependency dependency)
    throws IOException, ReflectiveOperationException {
    final var downloaded = this.dependencyDownloader.download(dependency);
    if (downloaded == null) {
      return null;
    }
    return this.relocationHelper.relocate(dependency, downloaded);
  }
}
