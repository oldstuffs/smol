package io.github.portlek.smol.relocation.helper;

import io.github.portlek.smol.resolver.data.Dependency;
import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public interface RelocationHelper {
  @NotNull
  File relocate(@NotNull Dependency dependency, @NotNull File input)
    throws IOException, ReflectiveOperationException;
}
