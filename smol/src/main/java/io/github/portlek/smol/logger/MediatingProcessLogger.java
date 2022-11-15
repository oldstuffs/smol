package io.github.portlek.smol.logger;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class MediatingProcessLogger implements ProcessLogger {

  @NotNull
  Collection<ProcessLogger> loggers;

  public void addLogger(@NotNull final ProcessLogger logger) {
    this.loggers.add(logger);
  }

  public void clearLoggers() {
    this.loggers.clear();
  }

  @Override
  public void debug(
    @NotNull final String message,
    @NotNull final Object @NotNull... args
  ) {
    for (final var logger : this.loggers) {
      logger.debug(message, args);
    }
  }

  @Override
  public void log(
    @NotNull final String message,
    @NotNull final Object @NotNull... args
  ) {
    for (final var logger : this.loggers) {
      logger.log(message, args);
    }
  }

  public void removeLogger(@NotNull final ProcessLogger logger) {
    this.loggers.remove(logger);
  }
}
