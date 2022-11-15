package io.github.portlek.smol.injector.loader.manifest;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class JarManifestGenerator implements ManifestGenerator {

  Map<String, String> attributes = new HashMap<>();

  @NotNull
  URI jarURI;

  @NotNull
  public static ManifestGenerator with(@NotNull final URI uri) {
    return new JarManifestGenerator(uri);
  }

  @NotNull
  @Override
  public ManifestGenerator attribute(
    @NotNull final String key,
    @NotNull final String value
  ) {
    this.attributes.put(key, value);
    return this;
  }

  @Override
  public void generate() throws IOException {
    final var env = new HashMap<String, String>();
    env.put("create", "true");
    final var uri = URI.create(String.format("jar:%s", this.jarURI));
    try (final var fs = FileSystems.newFileSystem(uri, env)) {
      final var nf = fs.getPath("META-INF/MANIFEST.MF");
      Files.createDirectories(nf.getParent());
      try (
        final var writer = Files.newBufferedWriter(
          nf,
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE
        )
      ) {
        for (final var entry : this.attributes.entrySet()) {
          writer.write(
            String.format("%s: %s%n", entry.getKey(), entry.getValue())
          );
        }
      }
    }
  }
}
