package io.github.portlek.smol.relocation;

import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public interface Relocator {
  void relocate(@NotNull File input, @NotNull File output)
    throws IOException, ReflectiveOperationException;
}
