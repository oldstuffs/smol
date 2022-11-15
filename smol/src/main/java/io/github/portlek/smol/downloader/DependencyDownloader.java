package io.github.portlek.smol.downloader;

import io.github.portlek.smol.resolver.data.Dependency;
import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DependencyDownloader {
  @Nullable
  File download(@NotNull Dependency dependency) throws IOException;
}
