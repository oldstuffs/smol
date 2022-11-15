package io.github.portlek.smol.relocation;

import java.util.Collection;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;

public record RelocationRule(
  @NotNull String originalPackagePattern,
  @NotNull String relocatedPackagePattern,
  @NotNull Collection<String> exclusions,
  @NotNull Collection<String> inclusions
) {
  public RelocationRule(
    @NotNull final String originalPackagePattern,
    @NotNull final String relocatedPackagePattern
  ) {
    this(
      originalPackagePattern,
      relocatedPackagePattern,
      Collections.emptyList(),
      Collections.emptyList()
    );
  }
}
