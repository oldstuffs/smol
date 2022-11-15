package io.github.portlek.smol.resolver.reader.dependency;

import io.github.portlek.smol.resolver.data.DependencyData;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;

public record ModuleDependencyDataProvider(
  @NotNull DependencyReader dependencyReader,
  @NotNull URL moduleUrl
)
  implements DependencyDataProvider {
  @NotNull
  @Override
  public DependencyData get() throws IOException, ReflectiveOperationException {
    final var depFileURL = new URL(
      "jar:file:" + this.moduleUrl.getFile() + "!/smol.json"
    );
    final var connection = depFileURL.openConnection();
    if (!(connection instanceof final JarURLConnection jarURLConnection)) {
      throw new AssertionError("Invalid Module URL provided(Non-Jar File)");
    }
    final var jarFile = jarURLConnection.getJarFile();
    final var dependencyFileEntry = jarFile.getEntry("smol.json");
    if (dependencyFileEntry == null) {
      return new DependencyData(
        Collections.emptySet(),
        Collections.emptySet(),
        Collections.emptySet(),
        Collections.emptySet()
      );
    }
    try (final var inputStream = jarFile.getInputStream(dependencyFileEntry)) {
      return this.dependencyReader.read(inputStream);
    }
  }
}
