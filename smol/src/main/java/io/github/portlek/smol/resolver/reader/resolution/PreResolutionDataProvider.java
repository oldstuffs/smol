package io.github.portlek.smol.resolver.reader.resolution;

import io.github.portlek.smol.resolver.ResolutionResult;
import java.io.IOException;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface PreResolutionDataProvider {
  @NotNull
  Map<String, ResolutionResult> get()
    throws IOException, ReflectiveOperationException;
}
