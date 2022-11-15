package io.github.portlek.smol.resolver.reader.dependency;

import io.github.portlek.smol.resolver.data.DependencyData;
import java.io.IOException;
import java.net.URL;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class URLDependencyDataProvider implements DependencyDataProvider {

  @NotNull
  URL depFileURL;

  @NotNull
  DependencyReader dependencyReader;

  @Getter
  @Nullable
  @NonFinal
  private DependencyData cachedData;

  @NotNull
  @Override
  public DependencyData get() throws IOException, ReflectiveOperationException {
    if (this.cachedData != null) {
      return this.cachedData;
    }
    try (final var is = this.depFileURL.openStream()) {
      return this.cachedData = this.dependencyReader.read(is);
    }
  }
}
