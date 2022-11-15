package io.github.portlek.smol.injector.loader;

import io.github.portlek.smol.injector.agent.ByteBuddyInstrumentationFactory;
import io.github.portlek.smol.injector.agent.InstrumentationFactory;
import io.github.portlek.smol.relocation.facade.ReflectiveJarRelocatorFacadeFactory;
import io.github.portlek.smol.resolver.data.Repository;
import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.jar.JarFile;
import org.jetbrains.annotations.NotNull;

public record InstrumentationInjectable(
  @NotNull Instrumentation instrumentation
)
  implements Injectable {
  @NotNull
  public static Injectable create(
    @NotNull final Path downloadPath,
    @NotNull final Collection<Repository> repositories
  )
    throws IOException, NoSuchAlgorithmException, ReflectiveOperationException, URISyntaxException {
    return InstrumentationInjectable.create(
      new ByteBuddyInstrumentationFactory(
        ReflectiveJarRelocatorFacadeFactory.create(downloadPath, repositories)
      )
    );
  }

  @NotNull
  public static Injectable create(
    @NotNull final InstrumentationFactory factory
  )
    throws IOException, NoSuchAlgorithmException, ReflectiveOperationException, URISyntaxException {
    return new InstrumentationInjectable(factory.create());
  }

  @Override
  public void inject(@NotNull final URL url)
    throws IOException, URISyntaxException {
    this.instrumentation.appendToSystemClassLoaderSearch(
        new JarFile(new File(url.toURI()))
      );
  }
}
