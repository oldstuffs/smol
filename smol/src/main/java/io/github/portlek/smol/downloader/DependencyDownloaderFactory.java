package io.github.portlek.smol.downloader;

import io.github.portlek.smol.downloader.output.OutputWriterFactory;
import io.github.portlek.smol.downloader.verify.DependencyVerifier;
import io.github.portlek.smol.resolver.DependencyResolver;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface DependencyDownloaderFactory {
  @NotNull
  DependencyDownloader create(
    @NotNull OutputWriterFactory outputWriterFactory,
    @NotNull DependencyResolver resolver,
    @NotNull DependencyVerifier verifier
  );
}
