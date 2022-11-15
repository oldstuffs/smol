package io.github.portlek.smol.resolver.data;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Dependency(
  @NotNull String groupId,
  @NotNull String artifactId,
  @NotNull String version,
  @Nullable String snapshotId,
  @NotNull Collection<Dependency> transitive
) {
  @NotNull
  public String dependencyNotation() {
    final var suffix = this.snapshotId != null && this.snapshotId.length() > 0
      ? ":" + this.snapshotId
      : "";
    return this.groupId + ":" + this.artifactId + ":" + this.version + suffix;
  }
}
