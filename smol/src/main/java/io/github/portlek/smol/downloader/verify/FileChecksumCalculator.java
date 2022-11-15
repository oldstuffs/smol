package io.github.portlek.smol.downloader.verify;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

public record FileChecksumCalculator(@NotNull MessageDigest digest)
  implements ChecksumCalculator {
  private static final String DIRECTORY_HASH = "DIRECTORY";

  private static final Logger LOGGER = Logger.getLogger(
    FileChecksumCalculator.class.getName()
  );

  public FileChecksumCalculator(@NotNull final String algorithm)
    throws NoSuchAlgorithmException {
    this(MessageDigest.getInstance(algorithm));
  }

  @NotNull
  @Override
  public String calculate(@NotNull final File file) throws IOException {
    FileChecksumCalculator.LOGGER.log(
      Level.FINEST,
      "Calculating hash for {0}",
      file.getPath()
    );
    if (file.isDirectory()) {
      return FileChecksumCalculator.DIRECTORY_HASH;
    }
    this.digest.reset();
    try (final var fis = new FileInputStream(file)) {
      final var byteArray = new byte[1024];
      int bytesCount;
      while ((bytesCount = fis.read(byteArray)) != -1) {
        this.digest.update(byteArray, 0, bytesCount);
      }
    }
    final var bytes = this.digest.digest();
    final var sb = new StringBuilder();
    for (final var b : bytes) {
      sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
    }
    sb.trimToSize();
    final var result = sb.toString();
    FileChecksumCalculator.LOGGER.log(
      Level.FINEST,
      "Hash for {0} -> {1}",
      new Object[] { file.getPath(), result }
    );
    return result;
  }
}
