package io.github.portlek.smol.resolver.reader.facade;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;

public interface GsonFacade {
  @NotNull
  <T> T fromJson(@NotNull InputStreamReader reader, @NotNull Class<T> clazz)
    throws ReflectiveOperationException;

  @NotNull
  <T> T fromJson(@NotNull InputStreamReader reader, @NotNull Type rawType)
    throws ReflectiveOperationException;
}
