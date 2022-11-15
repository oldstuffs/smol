package io.github.portlek.smol.resolver.strategy;

import io.github.portlek.smol.misc.Repositories;
import io.github.portlek.smol.resolver.data.Dependency;
import io.github.portlek.smol.resolver.data.Repository;
import java.util.Collection;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;

public final class MavenPomPathResolutionStrategy
  implements PathResolutionStrategy {

  private static final String PATH_FORMAT = "%s%s/%s/%s/%3$s-%4$s.pom";

  @NotNull
  @Override
  public Collection<String> pathTo(
    @NotNull final Repository repository,
    final Dependency dependency
  ) {
    final String repoUrl = Repositories.fetchFormattedUrl(repository);
    return Collections.singleton(
      String.format(
        MavenPomPathResolutionStrategy.PATH_FORMAT,
        repoUrl,
        dependency.groupId().replace('.', '/'),
        dependency.artifactId(),
        dependency.version()
      )
    );
  }
}
