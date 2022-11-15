package io.github.portlek.smol.resolver.data;

import com.sun.jdi.Mirror;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public record DependencyData(
  @NotNull Collection<Dependency> dependencies,
  @NotNull Collection<Mirror> mirrors,
  @NotNull Collection<Repository> repositories
) {}
