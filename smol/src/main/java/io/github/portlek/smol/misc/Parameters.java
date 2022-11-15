package io.github.portlek.smol.misc;

import java.util.Arrays;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Parameters {
  @NotNull
  static Class<?>@NotNull[] typesFrom(@Nullable final Object @NotNull... args) {
    return Arrays
      .stream(args, 0, args.length)
      .filter(Objects::nonNull)
      .map(Object::getClass)
      .toArray(Class[]::new);
  }
}
