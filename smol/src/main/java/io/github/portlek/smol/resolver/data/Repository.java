package io.github.portlek.smol.resolver.data;

import java.net.MalformedURLException;
import java.net.URL;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Repository(@NotNull URL url) {
  private static final String CENTRAL_URL = "https://repo1.maven.org/maven2/";

  @Nullable
  private static Repository central;

  @NotNull
  public static Repository central() throws MalformedURLException {
    if (Repository.central == null) {
      Repository.central = new Repository(new URL(Repository.CENTRAL_URL));
    }
    return Repository.central;
  }
}
