package io.github.portlek.smol.relocation.meta;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;

public final class FlatFileMetaMediatorFactory implements MetaMediatorFactory {

  @NotNull
  @Override
  public MetaMediator create(@NotNull final Path path) {
    final var metaPath = path
      .getParent()
      .resolve(path.getFileName().toString() + ".smol_meta");
    if (!Files.exists(metaPath)) {
      try {
        Files.createDirectories(metaPath);
      } catch (final IOException exception) {
        exception.printStackTrace();
      }
    }
    return new FlatFileMetaMediator(metaPath);
  }
}
