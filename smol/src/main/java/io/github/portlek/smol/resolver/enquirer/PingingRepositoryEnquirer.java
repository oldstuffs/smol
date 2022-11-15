package io.github.portlek.smol.resolver.enquirer;

import io.github.portlek.smol.logger.LogDispatcher;
import io.github.portlek.smol.logger.ProcessLogger;
import io.github.portlek.smol.resolver.ResolutionResult;
import io.github.portlek.smol.resolver.data.Dependency;
import io.github.portlek.smol.resolver.data.Repository;
import io.github.portlek.smol.resolver.pinger.URLPinger;
import io.github.portlek.smol.resolver.strategy.PathResolutionStrategy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PingingRepositoryEnquirer(
  @NotNull Repository repository,
  @NotNull PathResolutionStrategy dependencyURLCreationStrategy,
  @NotNull PathResolutionStrategy checksumURLCreationStrategy,
  @NotNull PathResolutionStrategy pomURLCreationStrategy,
  @NotNull URLPinger urlPinger
)
  implements RepositoryEnquirer {
  private static final ProcessLogger LOGGER = LogDispatcher.getMediatingLogger();

  @Nullable
  @Override
  public ResolutionResult enquire(@NotNull final Dependency dependency) {
    PingingRepositoryEnquirer.LOGGER.debug(
      "Enquiring repositories to find {0}",
      dependency.artifactId()
    );
    final var resolvedDependency =
      this.dependencyURLCreationStrategy.pathTo(this.repository, dependency)
        .stream()
        .map(path -> {
          try {
            return new URL(path);
          } catch (final MalformedURLException e) {
            return null;
          }
        })
        .filter(Objects::nonNull)
        .filter(this.urlPinger::ping)
        .findFirst();
    if (resolvedDependency.isEmpty()) {
      return this.pomURLCreationStrategy.pathTo(this.repository, dependency)
        .stream()
        .map(path -> {
          try {
            return new URL(path);
          } catch (final MalformedURLException e) {
            return null;
          }
        })
        .filter(Objects::nonNull)
        .filter(this.urlPinger::ping)
        .findFirst()
        .map(url -> new ResolutionResult(this.repository, null, null, true))
        .orElse(null);
    }
    final var resolvedChecksum =
      this.checksumURLCreationStrategy.pathTo(this.repository, dependency)
        .stream()
        .map(path -> {
          try {
            return new URL(path);
          } catch (final MalformedURLException e) {
            return null;
          }
        })
        .filter(Objects::nonNull)
        .filter(this.urlPinger::ping)
        .findFirst();
    return new ResolutionResult(
      this.repository,
      resolvedDependency.get(),
      resolvedChecksum.orElse(null),
      false
    );
  }
}
