package io.github.portlek.smol.downloader.output;

import io.github.portlek.smol.downloader.strategy.FilePathStrategy;
import io.github.portlek.smol.resolver.data.Dependency;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

public record DependencyOutputWriterFactory(
  @NotNull FilePathStrategy outputFilePathStrategy
)
  implements OutputWriterFactory {
  private static final Logger LOGGER = Logger.getLogger(
    DependencyOutputWriterFactory.class.getName()
  );

  @NotNull
  @Override
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public OutputWriter create(@NotNull final Dependency dependency) {
    DependencyOutputWriterFactory.LOGGER.log(
      Level.FINEST,
      "Creating OutputWriter for {0}",
      dependency.artifactId()
    );
    final var outputFile =
      this.outputFilePathStrategy.selectFileFor(dependency);
    outputFile.getParentFile().mkdirs();
    return new ChanneledFileOutputWriter(outputFile);
  }

  @Override
  public FilePathStrategy getStrategy() {
    return this.outputFilePathStrategy;
  }
}
