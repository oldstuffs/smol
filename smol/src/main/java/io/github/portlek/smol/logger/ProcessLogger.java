package io.github.portlek.smol.logger;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ProcessLogger {
  default void debug(
    @NotNull final String message,
    @NotNull final Object @NotNull... args
  ) {}

  void log(@NotNull String message, @NotNull Object @NotNull... args);
}
