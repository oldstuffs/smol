package io.github.portlek.smol.resolver;

import io.github.portlek.smol.resolver.data.Repository;
import java.net.URL;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public record ResolutionResult(
  @Nullable Repository repository,
  @Nullable URL dependencyURL,
  @Nullable URL checksumURL,
  boolean isAggregator
) {
  public ResolutionResult {
    if (!isAggregator) {
      Objects.requireNonNull(
        dependencyURL,
        "Resolved URL must not be null for non-aggregator dependencies"
      );
    }
  }
}
