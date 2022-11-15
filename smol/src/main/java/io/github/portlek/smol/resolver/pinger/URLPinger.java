package io.github.portlek.smol.resolver.pinger;

import java.net.URL;
import org.jetbrains.annotations.NotNull;

public interface URLPinger {
  boolean isSupported(@NotNull URL url);

  boolean ping(@NotNull URL url);
}
