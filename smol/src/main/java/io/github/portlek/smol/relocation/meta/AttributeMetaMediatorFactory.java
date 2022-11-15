package io.github.portlek.smol.relocation.meta;

import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;

public final class AttributeMetaMediatorFactory implements MetaMediatorFactory {

  @NotNull
  @Override
  public MetaMediator create(@NotNull final Path path) {
    return new AttributeMetaMediator(path);
  }
}
