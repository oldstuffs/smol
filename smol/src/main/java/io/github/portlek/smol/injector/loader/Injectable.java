package io.github.portlek.smol.injector.loader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import org.jetbrains.annotations.NotNull;

public interface Injectable {
  @NotNull
  static WrappedInjectableClassLoader wrap(
    @NotNull final URLClassLoader classLoader
  ) {
    return new WrappedInjectableClassLoader(classLoader);
  }

  void inject(@NotNull URL url)
    throws IOException, InvocationTargetException, IllegalAccessException, URISyntaxException;
}
