package io.github.portlek.smol.downloader.verify;

import io.github.portlek.smol.resolver.DependencyResolver;
import org.jetbrains.annotations.NotNull;

public interface DependencyVerifierFactory {
  @NotNull
  DependencyVerifier create(@NotNull DependencyResolver resolver);
}
