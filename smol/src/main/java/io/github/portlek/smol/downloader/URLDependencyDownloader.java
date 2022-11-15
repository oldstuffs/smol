package io.github.portlek.smol.downloader;

import io.github.portlek.smol.downloader.output.OutputWriterFactory;
import io.github.portlek.smol.downloader.verify.DependencyVerifier;
import io.github.portlek.smol.logger.LogDispatcher;
import io.github.portlek.smol.logger.ProcessLogger;
import io.github.portlek.smol.misc.Connections;
import io.github.portlek.smol.resolver.DependencyResolver;
import io.github.portlek.smol.resolver.UnresolvedDependencyException;
import io.github.portlek.smol.resolver.data.Dependency;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record URLDependencyDownloader(
  @NotNull OutputWriterFactory outputWriterProducer,
  @NotNull DependencyResolver dependencyResolver,
  @NotNull DependencyVerifier verifier
)
  implements DependencyDownloader {
  private static final byte[] BOM_BYTES = "bom-file".getBytes();

  private static final ProcessLogger LOGGER = LogDispatcher.getMediatingLogger();

  @Nullable
  @Override
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public File download(@NotNull final Dependency dependency)
    throws IOException {
    final var expectedOutputFile =
      this.outputWriterProducer.getStrategy().selectFileFor(dependency);
    if (
      expectedOutputFile.exists() &&
      expectedOutputFile.length() == URLDependencyDownloader.BOM_BYTES.length &&
      Arrays.equals(
        Files.readAllBytes(expectedOutputFile.toPath()),
        URLDependencyDownloader.BOM_BYTES
      )
    ) {
      return null;
    }
    if (this.verifier.verify(expectedOutputFile, dependency)) {
      return expectedOutputFile;
    }
    final var result =
      this.dependencyResolver.resolve(dependency)
        .orElseThrow(() -> new UnresolvedDependencyException(dependency));
    if (result.isAggregator()) {
      expectedOutputFile.getParentFile().mkdirs();
      expectedOutputFile.createNewFile();
      Files.write(
        expectedOutputFile.toPath(),
        URLDependencyDownloader.BOM_BYTES
      );
      return null;
    }
    expectedOutputFile.delete();
    final var checksumFile = this.verifier.getChecksumFile(dependency);
    if (checksumFile != null) {
      checksumFile.delete();
    }
    URLDependencyDownloader.LOGGER.log(
      "Downloading {0}...",
      dependency.artifactId()
    );
    final var url = Objects.requireNonNull(result.dependencyURL());
    URLDependencyDownloader.LOGGER.debug("Connecting to {0}", url);
    final var connection = Connections.createDownloadConnection(url);
    final var inputStream = connection.getInputStream();
    URLDependencyDownloader.LOGGER.debug(
      "Connection successful! Downloading {0}",
      dependency.artifactId() + "..."
    );
    final var outputWriter = this.outputWriterProducer.create(dependency);
    URLDependencyDownloader.LOGGER.debug(
      "{0}.Size = {1}",
      dependency.artifactId(),
      connection.getContentLength()
    );
    final var downloadResult = outputWriter.writeFrom(
      inputStream,
      connection.getContentLength()
    );
    Connections.tryDisconnect(connection);
    this.verifier.verify(downloadResult, dependency);
    URLDependencyDownloader.LOGGER.debug(
      "Artifact {0} downloaded successfully!",
      dependency.artifactId()
    );
    URLDependencyDownloader.LOGGER.log(
      "Downloaded {0} successfully!",
      dependency.artifactId()
    );
    return downloadResult;
  }
}
