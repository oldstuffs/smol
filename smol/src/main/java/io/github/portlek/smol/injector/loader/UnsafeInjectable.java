package io.github.portlek.smol.injector.loader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

@SuppressWarnings("unchecked")
public record UnsafeInjectable(
  @NotNull ArrayDeque<URL> unopenedURLs,
  @NotNull ArrayList<URL> pathURLs
)
  implements Injectable {
  @NotNull
  public static Injectable create(@NotNull final URLClassLoader classLoader)
    throws NoSuchFieldException, IllegalAccessException {
    final var field = Unsafe.class.getDeclaredField("theUnsafe");
    field.setAccessible(true);
    final var unsafe = (Unsafe) field.get(null);
    final var ucp = UnsafeInjectable.fetchField(
      unsafe,
      URLClassLoader.class,
      classLoader,
      "ucp"
    );
    final var unopenedURLs = (ArrayDeque<URL>) UnsafeInjectable.fetchField(
      unsafe,
      ucp,
      "unopenedUrls"
    );
    final var pathURLs = (ArrayList<URL>) UnsafeInjectable.fetchField(
      unsafe,
      ucp,
      "path"
    );
    return new UnsafeInjectable(unopenedURLs, pathURLs);
  }

  @NotNull
  private static Object fetchField(
    @NotNull final Unsafe unsafe,
    @NotNull final Object object,
    @NotNull final String name
  ) throws NoSuchFieldException {
    return UnsafeInjectable.fetchField(unsafe, object.getClass(), object, name);
  }

  @NotNull
  private static Object fetchField(
    @NotNull final Unsafe unsafe,
    @NotNull final Class<?> clazz,
    @NotNull final Object object,
    @NotNull final String name
  ) throws NoSuchFieldException {
    return unsafe.getObject(
      object,
      unsafe.objectFieldOffset(clazz.getDeclaredField(name))
    );
  }

  @Override
  public void inject(@NotNull final URL url) {
    this.unopenedURLs.addLast(url);
    this.pathURLs.add(url);
  }
}
