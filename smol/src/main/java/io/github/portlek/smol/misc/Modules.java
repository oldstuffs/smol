package io.github.portlek.smol.misc;

import io.github.portlek.smol.app.module.ModuleExtractor;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Modules {
  @NotNull
  static URL@NotNull[] extract(
    @NotNull final ModuleExtractor extractor,
    @NotNull final Collection<String> modules
  ) throws IOException {
    final var urls = new URL[modules.size()];
    var index = 0;
    for (final var moduleName : modules) {
      final var modulePath = Modules.findModule(moduleName);
      if (modulePath == null) {
        continue;
      }
      urls[index++] = extractor.extractModule(modulePath, moduleName);
    }
    return urls;
  }

  @NotNull
  static Collection<String> findLocalModules()
    throws URISyntaxException, IOException {
    final var url =
      Modules.class.getProtectionDomain().getCodeSource().getLocation();
    final var resourcesPath = Paths.get(url.toURI());
    try (final var walk = Files.walk(resourcesPath, 1)) {
      return walk
        .filter(path -> path.endsWith(".isolated-jar"))
        .map(Path::getFileName)
        .map(Path::toString)
        .collect(Collectors.toSet());
    }
  }

  @Nullable
  static URL findModule(@NotNull final String moduleName) {
    return Modules.class.getClassLoader()
      .getResource(moduleName + ".isolated-jar");
  }
}
