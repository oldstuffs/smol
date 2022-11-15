package io.github.portlek.smol.resolver.pinger;

import io.github.portlek.smol.logger.LogDispatcher;
import io.github.portlek.smol.logger.ProcessLogger;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public final class HttpURLPinger implements URLPinger {

  private static final ProcessLogger LOGGER = LogDispatcher.getMediatingLogger();

  private static final String SMOL_USER_AGENT =
    "SmolApplication/* URL Validation Ping";

  private static final Collection<String> SUPPORTED_PROTOCOLS = Arrays.asList(
    "HTTP",
    "HTTPS"
  );

  @Override
  public boolean isSupported(@NotNull final URL url) {
    final var protocol = url.getProtocol().toUpperCase(Locale.ENGLISH);
    return HttpURLPinger.SUPPORTED_PROTOCOLS.contains(protocol);
  }

  @Override
  public boolean ping(@NotNull final URL url) {
    final var urlStr = url.toString();
    HttpURLPinger.LOGGER.debug("Pinging {0}", urlStr);
    if (!this.isSupported(url)) {
      HttpURLPinger.LOGGER.debug(
        "Protocol not supported for {0}",
        url.toString()
      );
      return false;
    }
    HttpURLConnection connection = null;
    try {
      connection = (HttpURLConnection) url.openConnection();
      connection.setConnectTimeout(1000 * 5);
      connection.addRequestProperty(
        "User-Agent",
        HttpURLPinger.SMOL_USER_AGENT
      );
      connection.connect();
      final var result =
        connection.getResponseCode() == HttpURLConnection.HTTP_OK;
      HttpURLPinger.LOGGER.debug(
        "Ping {1} for {0}",
        url.toString(),
        result ? "successful" : "failed"
      );
      return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
    } catch (final IOException e) {
      HttpURLPinger.LOGGER.debug("Ping failed for {0}", url.toString());
      return false;
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }
}
