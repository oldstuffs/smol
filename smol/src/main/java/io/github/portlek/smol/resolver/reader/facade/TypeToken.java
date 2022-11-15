package io.github.portlek.smol.resolver.reader.facade;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class TypeToken<T> {

  @NotNull
  Type rawType;

  protected TypeToken() {
    this.rawType = TypeToken.getSuperclassTypeParameter(this.getClass());
  }

  @NotNull
  private static Type getSuperclassTypeParameter(
    @NotNull final Class<?> subclass
  ) {
    final var spr = subclass.getGenericSuperclass();
    if (!(spr instanceof final ParameterizedType parameterized)) {
      throw new RuntimeException("Type parameter not found");
    }
    return parameterized.getActualTypeArguments()[0];
  }
}
