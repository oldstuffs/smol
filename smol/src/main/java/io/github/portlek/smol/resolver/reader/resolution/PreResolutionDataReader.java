package io.github.portlek.smol.resolver.reader.resolution;

import io.github.portlek.smol.resolver.ResolutionResult;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public interface PreResolutionDataReader {
  @NotNull
  Map<String, ResolutionResult> read(@NotNull InputStream inputStream)
    throws IOException, ReflectiveOperationException;
}
