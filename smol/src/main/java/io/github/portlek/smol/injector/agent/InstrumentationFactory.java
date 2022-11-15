package io.github.portlek.smol.injector.agent;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import org.jetbrains.annotations.NotNull;

public interface InstrumentationFactory {
  @NotNull
  Instrumentation create()
    throws IOException, ReflectiveOperationException, URISyntaxException, NoSuchAlgorithmException;
}
