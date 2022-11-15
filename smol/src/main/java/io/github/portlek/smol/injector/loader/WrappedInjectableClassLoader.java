package io.github.portlek.smol.injector.loader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class WrappedInjectableClassLoader implements Injectable {

  @Nullable
  Method addURLMethod;

  @NotNull
  URLClassLoader urlClassLoader;

  public WrappedInjectableClassLoader(
    @NotNull final URLClassLoader urlClassLoader
  ) {
    Method methodDefer;
    this.urlClassLoader = urlClassLoader;
    try {
      methodDefer = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
    } catch (final NoSuchMethodException e) {
      e.printStackTrace();
      methodDefer = null;
    }
    this.addURLMethod = methodDefer;
  }

  @Override
  public void inject(@NotNull final URL url)
    throws InvocationTargetException, IllegalAccessException {
    if (this.addURLMethod != null) {
      this.addURLMethod.setAccessible(true);
      this.addURLMethod.invoke(this.urlClassLoader, url);
    }
  }
}
