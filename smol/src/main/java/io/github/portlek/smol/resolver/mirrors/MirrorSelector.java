package io.github.portlek.smol.resolver.mirrors;

import io.github.portlek.smol.resolver.data.Mirror;
import io.github.portlek.smol.resolver.data.Repository;
import java.net.MalformedURLException;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public interface MirrorSelector {
  @NotNull
  Collection<Repository> select(
    @NotNull Collection<Repository> repositories,
    @NotNull Collection<Mirror> mirrors
  ) throws MalformedURLException;
}
