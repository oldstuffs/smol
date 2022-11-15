package io.github.portlek.smol.injector;

import io.github.portlek.smol.resolver.data.Dependency;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class InjectionFailedException extends RuntimeException {

  @NotNull
  Dependency dependency;

  public InjectionFailedException(
    @NotNull final Dependency dependency,
    @NotNull final Exception cause
  ) {
    super("Smol failed to inject dependency: name -> " + dependency, cause);
    this.dependency = dependency;
  }
}
