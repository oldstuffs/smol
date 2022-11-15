package io.github.portlek.smol.resolver.reader.resolution;

import java.net.URL;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface PreResolutionDataProviderFactory {
  @NotNull
  PreResolutionDataProvider create(@NotNull URL resolutionFileURL);
}
