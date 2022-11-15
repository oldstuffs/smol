package io.github.portlek.smol.injector.loader;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class IsolatedInjectableClassLoader extends InjectableClassLoader {

  Map<String, Class<?>> delegatesMap = new HashMap<>();

  public IsolatedInjectableClassLoader(
    @NotNull final URL@NotNull[] urls,
    @NotNull final ClassLoader parent,
    @NotNull final Collection<Class<?>> delegates
  ) {
    super(urls, parent);
    for (final var clazz : delegates) {
      this.delegatesMap.put(clazz.getName(), clazz);
    }
  }

  public IsolatedInjectableClassLoader(
    @NotNull final URL@NotNull[] urls,
    @NotNull final Collection<Class<?>> delegates
  ) {
    this(urls, ClassLoader.getSystemClassLoader().getParent(), delegates);
  }

  public IsolatedInjectableClassLoader(@NotNull final URL @NotNull... urls) {
    this(urls, Collections.emptySet());
  }

  public IsolatedInjectableClassLoader() {
    this(new URL[0]);
  }

  @Override
  protected Class<?> loadClass(
    @NotNull final String name,
    final boolean resolve
  ) throws ClassNotFoundException {
    final var loaded = this.findLoadedClass(name);
    if (loaded != null) {
      return loaded;
    }
    final var delegate = this.delegatesMap.get(name);
    if (delegate != null) {
      return delegate;
    }
    return super.loadClass(name, resolve);
  }
}
