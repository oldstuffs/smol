package io.github.portlek.smol.downloader.output;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;

public interface OutputWriter {
  @NotNull
  File writeFrom(@NotNull InputStream inputStream, long length)
    throws IOException;
}
