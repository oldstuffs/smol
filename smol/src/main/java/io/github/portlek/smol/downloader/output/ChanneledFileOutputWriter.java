package io.github.portlek.smol.downloader.output;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

public record ChanneledFileOutputWriter(@NotNull File outputFile)
  implements OutputWriter {
  private static final Logger LOGGER = Logger.getLogger(
    ChanneledFileOutputWriter.class.getName()
  );

  @NotNull
  @Override
  public File writeFrom(
    @NotNull final InputStream inputStream,
    final long length
  ) throws IOException {
    ChanneledFileOutputWriter.LOGGER.log(
      Level.FINE,
      "Attempting to write from inputStream..."
    );
    if (!this.outputFile.exists()) {
      ChanneledFileOutputWriter.LOGGER.log(
        Level.FINE,
        "Writing {0} bytes...",
        length == -1 ? "unknown" : length
      );
      Files.copy(inputStream, this.outputFile.toPath());
    }
    inputStream.close();
    return this.outputFile;
  }
}
