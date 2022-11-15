package io.github.portlek.smol.resolver.reader.resolution;

import io.github.portlek.smol.resolver.ResolutionResult;
import io.github.portlek.smol.resolver.reader.facade.GsonFacade;
import io.github.portlek.smol.resolver.reader.facade.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public record GsonPreResolutionDataReader(@NotNull GsonFacade gson)
  implements PreResolutionDataReader {
  @NotNull
  @Override
  public Map<String, ResolutionResult> read(
    @NotNull final InputStream inputStream
  ) throws ReflectiveOperationException {
    final var inputStreamReader = new InputStreamReader(inputStream);
    final var rawType = new TypeToken<Map<String, ResolutionResult>>()
      .rawType();
    return this.gson.fromJson(inputStreamReader, rawType);
  }
}
