package io.github.portlek.smol.relocation;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public interface RelocatorFactory {
  @NotNull
  Relocator create(@NotNull Collection<RelocationRule> rules);
}
