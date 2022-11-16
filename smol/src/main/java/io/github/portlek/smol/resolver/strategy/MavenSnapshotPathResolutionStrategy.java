package io.github.portlek.smol.resolver.strategy;

import io.github.portlek.smol.misc.Repositories;
import io.github.portlek.smol.resolver.data.Dependency;
import io.github.portlek.smol.resolver.data.Repository;
import java.util.Arrays;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public final class MavenSnapshotPathResolutionStrategy
  implements PathResolutionStrategy {

  private static final String PATH_FORMAT =
    "%s%s/%s/%s-SNAPSHOT/%3$s-%4$s-%5$s.jar";

  private static final String PATH_FORMAT_ALT =
    "%s%s/%s/%s-SNAPSHOT/%4$s-%s/%3$s-%4$s-%5$s.jar";

  @NotNull
  @Override
  public Collection<String> pathTo(
    @NotNull final Repository repository,
    @NotNull final Dependency dependency
  ) {
    final var repoUrl = Repositories.fetchFormattedUrl(repository);
    final var version = dependency.version().replace("-SNAPSHOT", "");
    final var alt = String.format(
      MavenSnapshotPathResolutionStrategy.PATH_FORMAT_ALT,
      repoUrl,
      dependency.groupId().replace('.', '/'),
      dependency.artifactId(),
      version,
      dependency.snapshotId()
    );
    final var general = String.format(
      MavenSnapshotPathResolutionStrategy.PATH_FORMAT,
      repoUrl,
      dependency.groupId().replace('.', '/'),
      dependency.artifactId(),
      version,
      dependency.snapshotId()
    );
    return Arrays.asList(general, alt);
  }
}
