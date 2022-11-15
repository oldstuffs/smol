package io.github.portlek.smol.downloader;

import io.github.portlek.smol.downloader.output.OutputWriterFactory;
import io.github.portlek.smol.downloader.verify.DependencyVerifier;
import io.github.portlek.smol.resolver.DependencyResolver;
import org.jetbrains.annotations.NotNull;

public final class URLDependencyDownloaderFactory
  implements DependencyDownloaderFactory {

  @NotNull
  @Override
  public DependencyDownloader create(
    @NotNull final OutputWriterFactory outputWriterFactory,
    @NotNull final DependencyResolver resolver,
    @NotNull final DependencyVerifier verifier
  ) {
    return new URLDependencyDownloader(outputWriterFactory, resolver, verifier);
  }
}
