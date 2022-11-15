package io.github.portlek.smol.resolver;

import io.github.portlek.smol.resolver.data.Repository;
import io.github.portlek.smol.resolver.enquirer.RepositoryEnquirerFactory;
import io.github.portlek.smol.resolver.pinger.URLPinger;
import java.util.Collection;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public record CachingDependencyResolverFactory(URLPinger urlPinger)
  implements DependencyResolverFactory {
  @Override
  public DependencyResolver create(
    @NotNull final Collection<Repository> repositories,
    @NotNull final Map<String, ResolutionResult> preResolvedResults,
    @NotNull final RepositoryEnquirerFactory enquirerFactory
  ) {
    return new CachingDependencyResolver(
      this.urlPinger,
      repositories,
      enquirerFactory,
      preResolvedResults
    );
  }
}
