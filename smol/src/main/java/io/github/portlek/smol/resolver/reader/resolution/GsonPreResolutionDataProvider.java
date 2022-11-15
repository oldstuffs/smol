package io.github.portlek.smol.resolver.reader.resolution;

import io.github.portlek.smol.resolver.ResolutionResult;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class GsonPreResolutionDataProvider
  implements PreResolutionDataProvider {

  @NotNull
  PreResolutionDataReader resolutionDataReader;

  @NotNull
  URL resolutionFileURL;

  @NonFinal
  @Nullable
  Map<String, ResolutionResult> cachedData;

  @NotNull
  @Override
  public Map<String, ResolutionResult> get()
    throws IOException, ReflectiveOperationException {
    if (this.cachedData != null) {
      return this.cachedData;
    }
    try (final var is = this.resolutionFileURL.openStream()) {
      return this.cachedData = this.resolutionDataReader.read(is);
    } catch (final Exception ignored) {}
    return Collections.emptyMap();
  }
}
