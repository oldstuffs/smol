package io.github.portlek.smol.resolver.strategy;

import io.github.portlek.smol.resolver.data.Dependency;
import io.github.portlek.smol.resolver.data.Repository;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public interface PathResolutionStrategy {
  @NotNull
  Collection<String> pathTo(
    @NotNull Repository repository,
    @NotNull Dependency dependency
  );
}
