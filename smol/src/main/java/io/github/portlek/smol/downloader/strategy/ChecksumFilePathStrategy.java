package io.github.portlek.smol.downloader.strategy;

import io.github.portlek.smol.resolver.data.Dependency;
import java.io.File;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class ChecksumFilePathStrategy implements FilePathStrategy {

  private static final String DEPENDENCY_FILE_FORMAT =
    "%s/%s/%s/%s/%3$s-%4$s.jar.%5$s";

  private static final Logger LOGGER = Logger.getLogger(
    FolderedFilePathStrategy.class.getName()
  );

  @NotNull
  String algorithm;

  @NotNull
  File rootDirectory;

  private ChecksumFilePathStrategy(
    @NotNull final File rootDirectory,
    @NotNull final String algorithm
  ) {
    this.rootDirectory = rootDirectory;
    this.algorithm =
      algorithm.replaceAll("[ -]", "").toLowerCase(Locale.ENGLISH);
  }

  @NotNull
  public static FilePathStrategy createStrategy(
    @NotNull final File rootDirectory,
    @NotNull final String algorithm
  ) throws IllegalArgumentException {
    if (!rootDirectory.exists() && !rootDirectory.mkdirs()) {
      throw new IllegalArgumentException(
        "Could not create specified directory: " + rootDirectory
      );
    }
    if (!rootDirectory.isDirectory()) {
      throw new IllegalArgumentException(
        "Expecting a directory for download root! " + rootDirectory
      );
    }
    return new ChecksumFilePathStrategy(rootDirectory, algorithm);
  }

  @NotNull
  @Override
  public File selectFileFor(@NotNull final Dependency dependency) {
    final var extendedVersion = Optional
      .ofNullable(dependency.snapshotId())
      .map(s -> "-" + s)
      .orElse("");
    final var path = String.format(
      ChecksumFilePathStrategy.DEPENDENCY_FILE_FORMAT,
      this.rootDirectory.getPath(),
      dependency.groupId().replace('.', '/'),
      dependency.artifactId(),
      dependency.version() + extendedVersion,
      this.algorithm
    );
    ChecksumFilePathStrategy.LOGGER.log(
      Level.FINEST,
      "Selected checksum file for " + dependency.artifactId() + " at " + path
    );
    return new File(path);
  }
}
