package io.github.portlek.smol.relocation.meta;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record FlatFileMetaMediator(@NotNull Path metaFolderPath)
  implements MetaMediator {
  @Nullable
  @Override
  public String readAttribute(@NotNull final String name) throws IOException {
    final var attributeFile = this.metaFolderPath.resolve(name);
    if (!Files.exists(attributeFile) || Files.isDirectory(attributeFile)) {
      return null;
    }
    return new String(Files.readAllBytes(attributeFile));
  }

  @Override
  public void writeAttribute(
    @NotNull final String name,
    @NotNull final String value
  ) throws IOException {
    final var attributeFile = this.metaFolderPath.resolve(name);
    Files.deleteIfExists(attributeFile);
    Files.createFile(attributeFile);
    Files.write(attributeFile, value.getBytes());
  }
}
