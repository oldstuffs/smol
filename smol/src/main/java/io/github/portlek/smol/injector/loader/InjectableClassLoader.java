package io.github.portlek.smol.injector.loader;

import java.net.URL;
import java.net.URLClassLoader;
import org.jetbrains.annotations.NotNull;

public abstract class InjectableClassLoader
  extends URLClassLoader
  implements Injectable {
  static {
    ClassLoader.registerAsParallelCapable();
  }

  protected InjectableClassLoader(
    @NotNull final URL@NotNull[] urls,
    @NotNull final ClassLoader parent
  ) {
    super(urls, parent);
  }

  @Override
  public final void inject(@NotNull final URL url) {
    this.addURL(url);
  }
}
