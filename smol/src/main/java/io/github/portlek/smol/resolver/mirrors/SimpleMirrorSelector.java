package io.github.portlek.smol.resolver.mirrors;

import io.github.portlek.smol.resolver.data.Mirror;
import io.github.portlek.smol.resolver.data.Repository;
import java.util.Collection;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public final class SimpleMirrorSelector implements MirrorSelector {

  @NotNull
  @Override
  public Collection<Repository> select(
    @NotNull final Collection<Repository> repositories,
    @NotNull final Collection<Mirror> mirrors
  ) {
    final var originals = mirrors
      .stream()
      .map(Mirror::original)
      .collect(Collectors.toSet());
    final var resolved = repositories
      .stream()
      .filter(repo -> !originals.contains(repo.url()))
      .collect(Collectors.toSet());
    final var mirrored = mirrors
      .stream()
      .map(Mirror::mirroring)
      .map(Repository::new)
      .collect(Collectors.toSet());
    resolved.addAll(mirrored);
    return resolved;
  }
}
