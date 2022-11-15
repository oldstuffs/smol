package io.github.portlek.smol.downloader.verify;

import io.github.portlek.smol.resolver.data.Dependency;
import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DependencyVerifier {
  @Nullable
  File getChecksumFile(@NotNull Dependency dependency);

  boolean verify(@NotNull File file, @NotNull Dependency dependency)
    throws IOException;
}
