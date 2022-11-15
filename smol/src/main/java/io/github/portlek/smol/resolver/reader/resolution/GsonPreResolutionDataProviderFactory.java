package io.github.portlek.smol.resolver.reader.resolution;

import io.github.portlek.smol.resolver.reader.facade.GsonFacade;
import io.github.portlek.smol.resolver.reader.facade.GsonFacadeFactory;
import java.net.URL;
import org.jetbrains.annotations.NotNull;

public record GsonPreResolutionDataProviderFactory(@NotNull GsonFacade gson)
  implements PreResolutionDataProviderFactory {
  public GsonPreResolutionDataProviderFactory(final GsonFacadeFactory gson)
    throws ReflectiveOperationException {
    this(gson.createFacade());
  }

  @NotNull
  @Override
  public PreResolutionDataProvider create(
    @NotNull final URL resolutionFileURL
  ) {
    return new GsonPreResolutionDataProvider(
      new GsonPreResolutionDataReader(this.gson),
      resolutionFileURL
    );
  }
}
