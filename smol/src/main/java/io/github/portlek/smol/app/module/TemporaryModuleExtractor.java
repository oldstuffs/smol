package io.github.portlek.smol.app.module;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.jetbrains.annotations.NotNull;

public final class TemporaryModuleExtractor implements ModuleExtractor {

  @NotNull
  @Override
  public URL extractModule(@NotNull final URL url, @NotNull final String name)
    throws IOException {
    final var tempFile = File.createTempFile(name, ".jar");
    tempFile.deleteOnExit();
    final var connection = url.openConnection();
    if (!(connection instanceof final JarURLConnection jarURLConnection)) {
      throw new AssertionError("Invalid Module URL provided(Non-Jar File)");
    }
    final var jarFile = jarURLConnection.getJarFile();
    final var module = jarFile.getJarEntry(name + ".isolated-jar");
    if (module == null) {
      throw new ModuleNotFoundException(
        "Could not find module in jar: " + name
      );
    }
    try (final var steam = jarFile.getInputStream(module)) {
      Files.copy(steam, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    return tempFile.toURI().toURL();
  }
}
