package io.github.portlek.smol.resolver.reader.facade;

import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public record ReflectiveGsonFacade(
  @NotNull Object gson,
  @NotNull Method gsonFromJsonMethod,
  @NotNull Method gsonFromJsonTypeMethod,
  @NotNull Method canonicalizeMethod
)
  implements GsonFacade {
  @NotNull
  @Override
  public <T> T fromJson(
    @NotNull final InputStreamReader reader,
    @NotNull final Class<T> clazz
  ) throws ReflectiveOperationException {
    final var result = this.gsonFromJsonMethod.invoke(this.gson, reader, clazz);
    if (!clazz.isAssignableFrom(result.getClass())) {
      throw new AssertionError("Gson returned wrong type!");
    }
    return (T) result;
  }

  @NotNull
  @Override
  public <T> T fromJson(
    @NotNull final InputStreamReader reader,
    @NotNull final Type rawType
  ) throws ReflectiveOperationException {
    final var type = this.canonicalizeMethod.invoke(null, rawType);
    return (T) this.gsonFromJsonTypeMethod.invoke(this.gson, reader, type);
  }
}
