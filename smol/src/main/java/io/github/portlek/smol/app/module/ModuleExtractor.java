package io.github.portlek.smol.app.module;

import java.io.IOException;
import java.net.URL;
import org.jetbrains.annotations.NotNull;

public interface ModuleExtractor {
  @NotNull
  URL extractModule(@NotNull URL url, @NotNull String name) throws IOException;
}
