package io.github.portlek.smol.resolver.reader.dependency;

import io.github.portlek.smol.resolver.data.DependencyData;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface DependencyDataProvider {
  @NotNull
  DependencyData get() throws IOException, ReflectiveOperationException;
}
