package io.github.portlek.smol.logger;

import java.util.HashSet;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class LogDispatcher {

  private final MediatingProcessLogger MEDIATING_LOGGER = new MediatingProcessLogger(
    new HashSet<>()
  );

  @NotNull
  public MediatingProcessLogger getMediatingLogger() {
    return LogDispatcher.MEDIATING_LOGGER;
  }
}
