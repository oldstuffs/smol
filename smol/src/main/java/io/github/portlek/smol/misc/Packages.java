package io.github.portlek.smol.misc;

import org.jetbrains.annotations.NotNull;

public interface Packages {
  @NotNull
  static String fix(@NotNull final String input) {
    return input.replace('#', '.');
  }
}
