package io.github.portlek.smol.resolver.enquirer;

import io.github.portlek.smol.resolver.ResolutionResult;
import io.github.portlek.smol.resolver.data.Dependency;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RepositoryEnquirer {
  @Nullable
  ResolutionResult enquire(@NotNull Dependency dependency);
}
