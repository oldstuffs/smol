package io.github.portlek.smol.resolver.data;

import io.github.portlek.smol.relocation.RelocationRule;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public record DependencyData(
  @NotNull Collection<Mirror> mirrors,
  @NotNull Collection<Repository> repositories,
  @NotNull Collection<Dependency> dependencies,
  @NotNull Collection<RelocationRule> relocations
) {}
