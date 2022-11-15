package io.github.portlek.smol.resolver.reader.dependency;

import io.github.portlek.smol.resolver.data.DependencyData;
import io.github.portlek.smol.resolver.reader.facade.GsonFacade;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.jetbrains.annotations.NotNull;

public record GsonDependencyReader(@NotNull GsonFacade gson)
  implements DependencyReader {
  @NotNull
  @Override
  public DependencyData read(@NotNull final InputStream inputStream)
    throws ReflectiveOperationException {
    return this.gson.fromJson(
        new InputStreamReader(inputStream),
        DependencyData.class
      );
  }
}
