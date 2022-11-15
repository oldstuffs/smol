package io.github.portlek.smol.injector.loader.manifest;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public interface ManifestGenerator {
  @NotNull
  ManifestGenerator attribute(@NotNull String key, @NotNull String value);

  void generate() throws IOException;
}
