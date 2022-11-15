package io.github.portlek.smol.relocation.meta;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record AttributeMetaMediator(@NotNull UserDefinedFileAttributeView view)
  implements MetaMediator {
  public AttributeMetaMediator(@NotNull final Path path) {
    this(Files.getFileAttributeView(path, UserDefinedFileAttributeView.class));
  }

  @Nullable
  @Override
  public String readAttribute(@NotNull final String name) {
    try {
      final var buf = ByteBuffer.allocate(this.view.size(name));
      this.view.read(name, buf);
      buf.flip();
      return Charset.defaultCharset().decode(buf).toString();
    } catch (final Exception exception) {
      return null;
    }
  }

  @Override
  public void writeAttribute(
    @NotNull final String name,
    @NotNull final String value
  ) {
    try {
      this.view.write(name, Charset.defaultCharset().encode(value));
    } catch (final Exception ignored) {}
  }
}
