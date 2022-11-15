package io.github.portlek.smol.relocation.helper;

import io.github.portlek.smol.downloader.strategy.FilePathStrategy;
import io.github.portlek.smol.relocation.Relocator;
import io.github.portlek.smol.relocation.meta.MetaMediatorFactory;
import io.github.portlek.smol.resolver.data.Dependency;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record VerifyingRelocationHelper(
  @Nullable String selfHash,
  @NotNull FilePathStrategy outputFilePathStrategy,
  @NotNull Relocator relocator,
  @NotNull MetaMediatorFactory mediatorFactory
)
  implements RelocationHelper {
  @NotNull
  @Override
  public File relocate(
    @NotNull final Dependency dependency,
    @NotNull final File input
  ) throws IOException, ReflectiveOperationException {
    final var relocatedFile =
      this.outputFilePathStrategy.selectFileFor(dependency);
    final var metaMediator =
      this.mediatorFactory.create(relocatedFile.toPath());
    if (relocatedFile.exists()) {
      try {
        final var ownerHash = metaMediator.readAttribute("smol.owner");
        if (
          this.selfHash != null &&
          ownerHash != null &&
          this.selfHash.trim().equals(ownerHash.trim())
        ) {
          return relocatedFile;
        }
      } catch (final Exception exception) {
        //noinspection ResultOfMethodCallIgnored
        relocatedFile.delete();
      }
    }
    this.relocator.relocate(input, relocatedFile);
    metaMediator.writeAttribute("smol.owner", Objects.toString(this.selfHash));
    return relocatedFile;
  }
}
