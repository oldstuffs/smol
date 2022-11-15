package io.github.portlek.smol.resolver.reader.dependency;

import java.net.URL;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface DependencyDataProviderFactory {
  @NotNull
  DependencyDataProvider create(@NotNull URL dependencyFileURL);
}
