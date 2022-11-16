package io.github.portlek.smol.downloader;

import io.github.portlek.smol.resolver.data.Dependency;
import java.util.function.Consumer;

@FunctionalInterface
public interface DownloadNotifier extends Consumer<Dependency> {}
