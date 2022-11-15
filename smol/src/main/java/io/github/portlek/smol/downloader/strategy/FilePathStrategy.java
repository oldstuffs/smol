package io.github.portlek.smol.downloader.strategy;

import io.github.portlek.smol.resolver.data.Dependency;
import java.io.File;
import org.jetbrains.annotations.NotNull;

public interface FilePathStrategy {
  @NotNull
  static FilePathStrategy createDefault(@NotNull final File root) {
    return FolderedFilePathStrategy.createStrategy(root);
  }

  @NotNull
  static FilePathStrategy createRelocationStrategy(
    @NotNull final File root,
    @NotNull final String applicationName
  ) {
    return RelocationFilePathStrategy.createStrategy(root, applicationName);
  }

  @NotNull
  File selectFileFor(@NotNull Dependency dependency);
}
