package io.github.portlek.smol.downloader.output;

import io.github.portlek.smol.downloader.strategy.FilePathStrategy;
import io.github.portlek.smol.resolver.data.Dependency;
import org.jetbrains.annotations.NotNull;

public interface OutputWriterFactory {
  @NotNull
  OutputWriter create(@NotNull Dependency dependency);

  @NotNull
  FilePathStrategy getStrategy();
}
