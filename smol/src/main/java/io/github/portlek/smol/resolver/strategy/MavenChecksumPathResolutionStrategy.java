package io.github.portlek.smol.resolver.strategy;

import io.github.portlek.smol.resolver.data.Dependency;
import io.github.portlek.smol.resolver.data.Repository;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class MavenChecksumPathResolutionStrategy
  implements PathResolutionStrategy {

  @NotNull
  String algorithm;

  @NotNull
  PathResolutionStrategy resolutionStrategy;

  public MavenChecksumPathResolutionStrategy(
    @NotNull final String algorithm,
    @NotNull final PathResolutionStrategy resolutionStrategy
  ) {
    this.algorithm =
      algorithm.replaceAll("[ -]", "").toLowerCase(Locale.ENGLISH);
    this.resolutionStrategy = resolutionStrategy;
  }

  @NotNull
  @Override
  public Collection<String> pathTo(
    @NotNull final Repository repository,
    @NotNull final Dependency dependency
  ) {
    return this.resolutionStrategy.pathTo(repository, dependency)
      .stream()
      .map(path -> path + "." + this.algorithm)
      .collect(Collectors.toSet());
  }
}
