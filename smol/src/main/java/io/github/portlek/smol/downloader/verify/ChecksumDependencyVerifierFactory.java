package io.github.portlek.smol.downloader.verify;

import io.github.portlek.smol.downloader.output.OutputWriterFactory;
import io.github.portlek.smol.resolver.DependencyResolver;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

public record ChecksumDependencyVerifierFactory(
  @NotNull OutputWriterFactory outputWriterFactory,
  @NotNull DependencyVerifierFactory fallbackVerifierFactory,
  @NotNull ChecksumCalculator checksumCalculator
)
  implements DependencyVerifierFactory {
  private static final Logger LOGGER = Logger.getLogger(
    ChecksumDependencyVerifierFactory.class.getName()
  );

  @NotNull
  @Override
  public DependencyVerifier create(@NotNull final DependencyResolver resolver) {
    ChecksumDependencyVerifierFactory.LOGGER.log(
      Level.FINEST,
      "Creating verifier..."
    );
    return new ChecksumDependencyVerifier(
      resolver,
      this.outputWriterFactory,
      this.fallbackVerifierFactory.create(resolver),
      this.checksumCalculator
    );
  }
}
