package io.github.portlek.smol.injector.helper;

import io.github.portlek.smol.downloader.DependencyDownloaderFactory;
import io.github.portlek.smol.downloader.output.DependencyOutputWriterFactory;
import io.github.portlek.smol.downloader.strategy.FilePathStrategy;
import io.github.portlek.smol.downloader.verify.DependencyVerifierFactory;
import io.github.portlek.smol.injector.DependencyInjectorFactory;
import io.github.portlek.smol.relocation.RelocatorFactory;
import io.github.portlek.smol.relocation.helper.RelocationHelperFactory;
import io.github.portlek.smol.resolver.DependencyResolverFactory;
import io.github.portlek.smol.resolver.ResolutionResult;
import io.github.portlek.smol.resolver.data.DependencyData;
import io.github.portlek.smol.resolver.enquirer.RepositoryEnquirerFactory;
import io.github.portlek.smol.resolver.mirrors.MirrorSelector;
import io.github.portlek.smol.resolver.reader.dependency.DependencyDataProviderFactory;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public record InjectionHelperFactory(
  @NotNull Path downloadDirectoryPath,
  @NotNull RelocatorFactory relocatorFactory,
  @NotNull DependencyDataProviderFactory dataProviderFactory,
  @NotNull RelocationHelperFactory relocationHelperFactory,
  @NotNull DependencyInjectorFactory injectorFactory,
  @NotNull DependencyResolverFactory resolverFactory,
  @NotNull RepositoryEnquirerFactory enquirerFactory,
  @NotNull DependencyDownloaderFactory downloaderFactory,
  @NotNull DependencyVerifierFactory verifier,
  @NotNull MirrorSelector mirrorSelector
) {
  @NotNull
  public InjectionHelper create(
    @NotNull final DependencyData data,
    @NotNull final Map<String, ResolutionResult> preResolvedResults
  ) throws IOException, NoSuchAlgorithmException, URISyntaxException {
    final var repositories =
      this.mirrorSelector.select(data.repositories(), data.mirrors());
    final var relocator = this.relocatorFactory.create(data.relocations());
    final var relocationHelper = this.relocationHelperFactory.create(relocator);
    final var filePathStrategy = FilePathStrategy.createDefault(
      this.downloadDirectoryPath.toFile()
    );
    final var outputWriterFactory = new DependencyOutputWriterFactory(
      filePathStrategy
    );
    final var resolver =
      this.resolverFactory.create(
          repositories,
          preResolvedResults,
          this.enquirerFactory
        );
    final var downloader =
      this.downloaderFactory.create(
          outputWriterFactory,
          resolver,
          this.verifier.create(resolver)
        );
    return new InjectionHelper(downloader, relocationHelper);
  }
}
