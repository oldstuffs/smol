package io.github.portlek.smol.misc;

import io.github.portlek.smol.resolver.data.Repository;
import org.jetbrains.annotations.NotNull;

public interface Repositories {
  @NotNull
  static String fetchFormattedUrl(@NotNull final Repository repository) {
    var repoUrl = repository.url().toString();
    if (!repoUrl.endsWith("/")) {
      repoUrl += "/";
    }
    return repoUrl;
  }
}
