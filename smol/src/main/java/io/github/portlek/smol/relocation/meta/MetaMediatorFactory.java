package io.github.portlek.smol.relocation.meta;

import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface MetaMediatorFactory {
  @NotNull
  MetaMediator create(@NotNull Path path);
}
