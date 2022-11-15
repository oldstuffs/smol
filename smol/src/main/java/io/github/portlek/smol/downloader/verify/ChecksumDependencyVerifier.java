package io.github.portlek.smol.downloader.verify;

import io.github.portlek.smol.downloader.output.OutputWriterFactory;
import io.github.portlek.smol.logger.LogDispatcher;
import io.github.portlek.smol.logger.ProcessLogger;
import io.github.portlek.smol.misc.Connections;
import io.github.portlek.smol.resolver.DependencyResolver;
import io.github.portlek.smol.resolver.data.Dependency;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public record ChecksumDependencyVerifier(
  @NotNull DependencyResolver resolver,
  @NotNull OutputWriterFactory outputWriterFactory,
  @NotNull DependencyVerifier fallbackVerifier,
  @NotNull ChecksumCalculator checksumCalculator
)
  implements DependencyVerifier {
  private static final ProcessLogger LOGGER = LogDispatcher.getMediatingLogger();

  @NotNull
  @Override
  public File getChecksumFile(@NotNull final Dependency dependency) {
    final var checksumFile =
      this.outputWriterFactory.getStrategy().selectFileFor(dependency);
    checksumFile.getParentFile().mkdirs();
    return checksumFile;
  }

  @Override
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public boolean verify(
    @NotNull final File file,
    @NotNull final Dependency dependency
  ) throws IOException {
    if (!file.exists()) {
      return false;
    }
    ChecksumDependencyVerifier.LOGGER.log(
      "Verifying checksum for {0}",
      dependency.artifactId()
    );
    final var checksumFile =
      this.outputWriterFactory.getStrategy().selectFileFor(dependency);
    checksumFile.getParentFile().mkdirs();
    if (
      !checksumFile.exists() &&
      !this.prepareChecksumFile(checksumFile, dependency)
    ) {
      ChecksumDependencyVerifier.LOGGER.log(
        "Unable to resolve checksum for {0}, falling back to fallbackVerifier!",
        dependency.artifactId()
      );
      return this.fallbackVerifier.verify(file, dependency);
    }
    if (checksumFile.length() == 0L) {
      ChecksumDependencyVerifier.LOGGER.log(
        "Required checksum not found for {0}, using fallbackVerifier!",
        dependency.artifactId()
      );
      return this.fallbackVerifier.verify(file, dependency);
    }
    final var actualChecksum = this.checksumCalculator.calculate(file);
    final var expectedChecksum = new String(
      Files.readAllBytes(checksumFile.toPath())
    )
      .trim();
    ChecksumDependencyVerifier.LOGGER.debug(
      "{0} -> Actual checksum: {1};",
      dependency.artifactId(),
      actualChecksum
    );
    ChecksumDependencyVerifier.LOGGER.debug(
      "{0} -> Expected checksum: {1};",
      dependency.artifactId(),
      expectedChecksum
    );
    final var match = Objects.equals(actualChecksum, expectedChecksum);
    ChecksumDependencyVerifier.LOGGER.log(
      "Checksum {0} for {1}",
      match ? "matched" : "match failed",
      dependency.artifactId()
    );
    return Objects.equals(actualChecksum, expectedChecksum);
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private boolean prepareChecksumFile(
    @NotNull final File checksumFile,
    @NotNull final Dependency dependency
  ) throws IOException {
    final var result = this.resolver.resolve(dependency);
    if (result.isEmpty()) {
      return false;
    }
    final var checkSumUrl = result.get().checksumURL();
    ChecksumDependencyVerifier.LOGGER.log(
      "Resolved checksum URL for {0} as {1}",
      dependency.artifactId(),
      Objects.toString(checkSumUrl)
    );
    if (checkSumUrl == null) {
      checksumFile.createNewFile();
      return true;
    }
    final var connection = Connections.createDownloadConnection(checkSumUrl);
    final var inputStream = connection.getInputStream();
    final var outputWriter = this.outputWriterFactory.create(dependency);
    outputWriter.writeFrom(inputStream, connection.getContentLength());
    Connections.tryDisconnect(connection);
    ChecksumDependencyVerifier.LOGGER.log(
      "Downloaded checksum for {0}",
      dependency.artifactId()
    );
    return true;
  }
}
