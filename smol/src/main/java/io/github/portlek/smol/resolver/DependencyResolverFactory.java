package io.github.portlek.smol.resolver;

import io.github.portlek.smol.resolver.data.Repository;
import io.github.portlek.smol.resolver.enquirer.RepositoryEnquirerFactory;
import java.util.Collection;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface DependencyResolverFactory {
  @NotNull
  DependencyResolver create(
    @NotNull Collection<Repository> repositories,
    @NotNull Map<String, ResolutionResult> preResolvedResults,
    @NotNull RepositoryEnquirerFactory enquirerFactory
  );
}
