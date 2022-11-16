package io.github.portlek.smol.downloader.strategy;

import io.github.portlek.smol.resolver.data.Dependency;
import java.io.File;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

public record RelocationFilePathStrategy(
  @NotNull String applicationName,
  @NotNull File rootDirectory
)
  implements FilePathStrategy {
  private static final String DEPENDENCY_FILE_FORMAT =
    "%s/%s/%s/%s/relocated/%5$s/%3$s-%4$s.jar";

  private static final Logger LOGGER = Logger.getLogger(
    FolderedFilePathStrategy.class.getName()
  );

  @NotNull
  public static FilePathStrategy createStrategy(
    @NotNull final File rootDirectory,
    @NotNull final String applicationName
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
    return new RelocationFilePathStrategy(applicationName, rootDirectory);
  }

  @NotNull
  @Override
  public File selectFileFor(@NotNull final Dependency dependency) {
    final var extendedVersion = Optional
      .ofNullable(dependency.snapshotId())
      .map(s -> "-" + s)
      .orElse("");
    final var path = String.format(
      RelocationFilePathStrategy.DEPENDENCY_FILE_FORMAT,
      this.rootDirectory.getPath(),
      dependency.groupId().replace('.', '/'),
      dependency.artifactId(),
      dependency.version() + extendedVersion,
      this.applicationName
    );
    RelocationFilePathStrategy.LOGGER.log(
      Level.FINEST,
      "Selected file for relocated " + dependency.artifactId() + " at " + path
    );
    return new File(path);
  }
}
