package io.github.portlek.smol.misc;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.jetbrains.annotations.NotNull;

public interface Connections {
  String SMOL_USER_AGENT = "SmolApplication/* URLDependencyDownloader";

  @NotNull
  static URLConnection createDownloadConnection(@NotNull final URL url)
    throws IOException {
    final var connection = url.openConnection();
    if (connection instanceof HttpURLConnection httpConnection) {
      connection.addRequestProperty("User-Agent", Connections.SMOL_USER_AGENT);
      final var responseCode = httpConnection.getResponseCode();
      if (responseCode != HttpURLConnection.HTTP_OK) {
        throw new IOException("Could not download from " + url);
      }
    }
    return connection;
  }

  static void tryDisconnect(@NotNull final URLConnection connection) {
    if (connection instanceof HttpURLConnection) {
      ((HttpURLConnection) connection).disconnect();
    }
  }
}
