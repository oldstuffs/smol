package io.github.portlek.smol.resolver.enquirer;

import io.github.portlek.smol.resolver.data.Repository;
import io.github.portlek.smol.resolver.pinger.URLPinger;
import io.github.portlek.smol.resolver.strategy.PathResolutionStrategy;
import org.jetbrains.annotations.NotNull;

public record PingingRepositoryEnquirerFactory(
  @NotNull PathResolutionStrategy checksumURLCreationStrategy,
  @NotNull PathResolutionStrategy pathResolutionStrategy,
  @NotNull PathResolutionStrategy pomURLCreationStrategy,
  @NotNull URLPinger urlPinger
)
  implements RepositoryEnquirerFactory {
  @NotNull
  @Override
  public RepositoryEnquirer create(@NotNull final Repository repository) {
    return new PingingRepositoryEnquirer(
      repository,
      this.pathResolutionStrategy,
      this.checksumURLCreationStrategy,
      this.pomURLCreationStrategy,
      this.urlPinger
    );
  }
}
