package io.github.portlek.smol.downloader.verify;

import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public interface ChecksumCalculator {
  @NotNull
  String calculate(@NotNull File file) throws IOException;
}
