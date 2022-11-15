package io.github.portlek.smol.resolver.reader.dependency;

import io.github.portlek.smol.resolver.data.DependencyData;
import java.io.IOException;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;

public interface DependencyReader {
  @NotNull
  DependencyData read(@NotNull InputStream inputStream)
    throws IOException, ReflectiveOperationException;
}
