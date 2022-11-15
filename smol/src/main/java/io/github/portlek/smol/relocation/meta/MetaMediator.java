package io.github.portlek.smol.relocation.meta;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MetaMediator {
  @Nullable
  String readAttribute(@NotNull String name) throws IOException;

  void writeAttribute(@NotNull String name, @NotNull String value)
    throws IOException;
}
