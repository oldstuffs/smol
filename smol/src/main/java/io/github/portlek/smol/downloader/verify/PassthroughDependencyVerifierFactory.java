package io.github.portlek.smol.downloader.verify;

import io.github.portlek.smol.resolver.DependencyResolver;
import io.github.portlek.smol.resolver.data.Dependency;
import java.io.File;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PassthroughDependencyVerifierFactory
  implements DependencyVerifierFactory {

  @NotNull
  @Override
  public DependencyVerifier create(@NotNull final DependencyResolver resolver) {
    return new PassthroughVerifier();
  }

  private static final class PassthroughVerifier implements DependencyVerifier {

    @Nullable
    @Override
    public File getChecksumFile(@NotNull final Dependency dependency) {
      return null;
    }

    @Override
    public boolean verify(
      @NotNull final File file,
      @NotNull final Dependency dependency
    ) {
      return file.exists();
    }
  }
}
