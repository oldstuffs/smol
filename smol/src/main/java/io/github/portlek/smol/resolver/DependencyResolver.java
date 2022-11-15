package io.github.portlek.smol.resolver;

import io.github.portlek.smol.resolver.data.Dependency;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface DependencyResolver {
  @NotNull
  Optional<ResolutionResult> resolve(@NotNull Dependency dependency);
}
