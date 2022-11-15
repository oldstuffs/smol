package io.github.portlek.smol.injector.agent;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

public final class ClassLoaderAgent {

  private static Instrumentation instrumentation;

  public static void agentmain(
    final String args,
    final Instrumentation instrumentation
  ) throws IOException {
    ClassLoaderAgent.instrumentation = instrumentation;
  }

  public static Instrumentation getInstrumentation() {
    return ClassLoaderAgent.instrumentation;
  }
}
