package io.github.portlek.smol.relocation.helper;

import io.github.portlek.smol.downloader.strategy.FilePathStrategy;
import io.github.portlek.smol.downloader.verify.FileChecksumCalculator;
import io.github.portlek.smol.relocation.Relocator;
import io.github.portlek.smol.relocation.meta.MetaMediatorFactory;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import org.jetbrains.annotations.NotNull;

public record VerifyingRelocationHelperFactory(
  @NotNull String selfHash,
  @NotNull FilePathStrategy relocationFilePathStrategy,
  @NotNull MetaMediatorFactory mediatorFactory
)
  implements RelocationHelperFactory {
  private static final URL JAR_URL =
    VerifyingRelocationHelperFactory.class.getProtectionDomain()
      .getCodeSource()
      .getLocation();

  public VerifyingRelocationHelperFactory(
    @NotNull final FileChecksumCalculator calculator,
    @NotNull final FilePathStrategy relocationFilePathStrategy,
    @NotNull final MetaMediatorFactory mediatorFactory
  ) throws URISyntaxException, IOException {
    this(
      calculator.calculate(
        new File(VerifyingRelocationHelperFactory.JAR_URL.toURI())
      ),
      relocationFilePathStrategy,
      mediatorFactory
    );
  }

  @NotNull
  @Override
  public RelocationHelper create(@NotNull final Relocator relocator)
    throws URISyntaxException, IOException, NoSuchAlgorithmException {
    return new VerifyingRelocationHelper(
      this.selfHash,
      this.relocationFilePathStrategy,
      relocator,
      this.mediatorFactory
    );
  }
}
