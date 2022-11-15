package io.github.portlek.smol.resolver;

import io.github.portlek.smol.resolver.data.Dependency;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class UnresolvedDependencyException extends RuntimeException {

  @NotNull
  Dependency dependency;

  public UnresolvedDependencyException(@NotNull final Dependency dependency) {
    super("Could not resolve dependency : " + dependency);
    this.dependency = dependency;
  }
}
