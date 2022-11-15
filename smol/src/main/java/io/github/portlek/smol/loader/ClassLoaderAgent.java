package io.github.portlek.smol.loader;

import java.lang.instrument.Instrumentation;
import org.jetbrains.annotations.NotNull;

public final class ClassLoaderAgent {

  private static Instrumentation instrumentation;

  public static void agentmain(
    @NotNull final String args,
    @NotNull final Instrumentation instrumentation
  ) {
    ClassLoaderAgent.instrumentation = instrumentation;
  }

  public static Instrumentation getInstrumentation() {
    return ClassLoaderAgent.instrumentation;
  }
}
