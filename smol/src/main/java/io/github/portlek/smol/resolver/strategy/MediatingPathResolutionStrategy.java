package io.github.portlek.smol.resolver.strategy;

import io.github.portlek.smol.resolver.data.Dependency;
import io.github.portlek.smol.resolver.data.Repository;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public record MediatingPathResolutionStrategy(
  @NotNull PathResolutionStrategy releaseStrategy,
  @NotNull PathResolutionStrategy snapshotStrategy
)
  implements PathResolutionStrategy {
  @NotNull
  @Override
  public Collection<String> pathTo(
    @NotNull final Repository repository,
    @NotNull final Dependency dependency
  ) {
    if (dependency.snapshotId() != null) {
      return this.snapshotStrategy.pathTo(repository, dependency);
    }
    return this.releaseStrategy.pathTo(repository, dependency);
  }
}
