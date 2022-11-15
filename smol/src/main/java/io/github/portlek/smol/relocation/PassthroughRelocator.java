package io.github.portlek.smol.relocation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.jetbrains.annotations.NotNull;

public final class PassthroughRelocator implements Relocator {

  @Override
  public void relocate(@NotNull final File input, @NotNull final File output)
    throws IOException {
    if (input.equals(output)) {
      return;
    }
    if (output.exists()) {
      return;
    }
    Files.copy(input.toPath(), output.toPath());
  }
}
