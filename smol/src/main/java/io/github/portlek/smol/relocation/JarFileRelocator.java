package io.github.portlek.smol.relocation;

import io.github.portlek.smol.relocation.facade.JarRelocatorFacadeFactory;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public record JarFileRelocator(
  @NotNull Collection<RelocationRule> relocations,
  @NotNull JarRelocatorFacadeFactory relocatorFacadeFactory
)
  implements Relocator {
  @Override
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void relocate(@NotNull final File input, @NotNull final File output)
    throws IOException, ReflectiveOperationException {
    output.getParentFile().mkdirs();
    output.createNewFile();
    this.relocatorFacadeFactory.createFacade(input, output, this.relocations)
      .run();
  }
}
