package io.github.portlek.smol.resolver.enquirer;

import io.github.portlek.smol.resolver.data.Repository;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface RepositoryEnquirerFactory {
  @NotNull
  RepositoryEnquirer create(@NotNull Repository repository);
}
