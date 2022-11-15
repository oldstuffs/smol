package io.github.portlek.smol.resolver;

import io.github.portlek.smol.logger.LogDispatcher;
import io.github.portlek.smol.logger.ProcessLogger;
import io.github.portlek.smol.resolver.data.Dependency;
import io.github.portlek.smol.resolver.data.Repository;
import io.github.portlek.smol.resolver.enquirer.RepositoryEnquirer;
import io.github.portlek.smol.resolver.enquirer.RepositoryEnquirerFactory;
import io.github.portlek.smol.resolver.pinger.URLPinger;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class CachingDependencyResolver implements DependencyResolver {

  private static final String FAILED_RESOLUTION_MESSAGE = "[FAILED TO RESOLVE]";

  private static final ProcessLogger LOGGER = LogDispatcher.getMediatingLogger();

  @NotNull
  Map<Dependency, ResolutionResult> cachedResults = new ConcurrentHashMap<>();

  @NotNull
  Map<String, ResolutionResult> preResolvedResults;

  @NotNull
  Collection<RepositoryEnquirer> repositories;

  @NotNull
  URLPinger urlPinger;

  public CachingDependencyResolver(
    @NotNull final URLPinger urlPinger,
    @NotNull final Collection<Repository> repositories,
    @NotNull final RepositoryEnquirerFactory enquirerFactory,
    @NotNull final Map<String, ResolutionResult> preResolvedResults
  ) {
    this.urlPinger = urlPinger;
    this.preResolvedResults = new ConcurrentHashMap<>(preResolvedResults);
    this.repositories =
      repositories
        .stream()
        .map(enquirerFactory::create)
        .collect(Collectors.toSet());
  }

  @NotNull
  @Override
  public Optional<ResolutionResult> resolve(
    @NotNull final Dependency dependency
  ) {
    return Optional.ofNullable(
      this.cachedResults.computeIfAbsent(dependency, this::attemptResolve)
    );
  }

  @Nullable
  private ResolutionResult attemptResolve(
    @NotNull final Dependency dependency
  ) {
    final var preResolvedResult =
      this.preResolvedResults.get(dependency.toString());
    if (preResolvedResult != null) {
      if (preResolvedResult.isAggregator()) {
        return preResolvedResult;
      }
      final var isDependencyURLValid =
        preResolvedResult.dependencyURL() != null &&
        this.urlPinger.ping(preResolvedResult.dependencyURL());
      final var checksumURL = preResolvedResult.checksumURL();
      final var isChecksumURLValid =
        checksumURL == null || this.urlPinger.ping(checksumURL);
      if (isDependencyURLValid && isChecksumURLValid) {
        return preResolvedResult;
      }
    }
    final var result =
      this.repositories.stream()
        .parallel()
        .map(enquirer -> enquirer.enquire(dependency))
        .filter(Objects::nonNull)
        .findFirst();
    final var resolvedResult = result
      .map(ResolutionResult::dependencyURL)
      .map(Objects::toString)
      .orElse(CachingDependencyResolver.FAILED_RESOLUTION_MESSAGE);
    CachingDependencyResolver.LOGGER.debug(
      "Resolved {0} @ {1}",
      dependency.artifactId(),
      resolvedResult
    );
    return result.orElse(null);
  }
}
