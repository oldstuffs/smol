package io.github.portlek.smol.relocation;

import io.github.portlek.smol.relocation.facade.JarRelocatorFacadeFactory;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public record JarFileRelocatorFactory(
  @NotNull JarRelocatorFacadeFactory relocatorFacadeFactory
)
  implements RelocatorFactory {
  @NotNull
  @Override
  public Relocator create(@NotNull final Collection<RelocationRule> rules) {
    return new JarFileRelocator(rules, this.relocatorFacadeFactory);
  }
}
