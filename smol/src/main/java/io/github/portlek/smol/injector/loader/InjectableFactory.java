package io.github.portlek.smol.injector.loader;

import io.github.portlek.smol.app.builder.ApplicationBuilder;
import io.github.portlek.smol.resolver.data.Repository;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class InjectableFactory {

  @NotNull
  public Injectable create(
    @NotNull final Path downloadPath,
    @NotNull final Collection<Repository> repositories
  )
    throws ReflectiveOperationException, NoSuchAlgorithmException, IOException, URISyntaxException {
    return InjectableFactory.create(
      downloadPath,
      repositories,
      InjectableFactory.class.getClassLoader()
    );
  }

  @NotNull
  public Injectable create(
    @NotNull final Path downloadPath,
    @NotNull final Collection<Repository> repositories,
    @NotNull final ClassLoader classLoader
  )
    throws URISyntaxException, ReflectiveOperationException, NoSuchAlgorithmException, IOException {
    final var isJigsawActive = InjectableFactory.isJigsawActive();
    Injectable injectable = null;
    if (isJigsawActive && classLoader instanceof URLClassLoader) {
      injectable =
        new WrappedInjectableClassLoader(
          (URLClassLoader) ApplicationBuilder.class.getClassLoader()
        );
    } else if (
      InjectableFactory.isUnsafeAvailable() &&
      classLoader instanceof URLClassLoader
    ) {
      try {
        injectable = UnsafeInjectable.create((URLClassLoader) classLoader);
      } catch (final Exception ignored) {}
    }
    if (injectable == null) {
      injectable = InstrumentationInjectable.create(downloadPath, repositories);
    }
    return injectable;
  }

  private boolean isJigsawActive() {
    try {
      Class.forName("java.lang.Module");
    } catch (final ClassNotFoundException e) {
      return true;
    }
    return false;
  }

  private boolean isUnsafeAvailable() {
    try {
      Class.forName("sun.misc.Unsafe");
    } catch (final ClassNotFoundException e) {
      return false;
    }
    return true;
  }
}
