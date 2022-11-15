package io.github.portlek.smol.app;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Nullable;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class Application {

  Map<Class<?>, Object> facadeMapping = new HashMap<>();

  @Nullable
  @SuppressWarnings("unchecked")
  public final <T> T getFacade(final Class<T> clazz) {
    final var obj = this.facadeMapping.get(clazz);
    if (obj == null) {
      return null;
    }
    if (!clazz.isAssignableFrom(obj.getClass())) {
      throw new IllegalStateException(
        "Current facade value does not conform to type restriction!"
      );
    }
    return (T) obj;
  }

  public boolean start() {
    return false;
  }

  public boolean stop() {
    return false;
  }

  @Nullable
  @SuppressWarnings("unchecked")
  protected final <T, U extends T> T registerFacade(
    final Class<T> clazz,
    final U t
  ) {
    final var obj = this.facadeMapping.put(clazz, t);
    if (obj != null && !clazz.isAssignableFrom(obj.getClass())) {
      throw new IllegalStateException(
        "Previous facade value did not conform to type restriction!"
      );
    }
    return (T) obj;
  }
}
