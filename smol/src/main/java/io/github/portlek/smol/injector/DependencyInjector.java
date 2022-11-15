package io.github.portlek.smol.injector;

import io.github.portlek.smol.injector.loader.Injectable;
import io.github.portlek.smol.resolver.ResolutionResult;
import io.github.portlek.smol.resolver.data.DependencyData;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public interface DependencyInjector {
  void inject(
    @NotNull Injectable injectable,
    @NotNull DependencyData data,
    @NotNull Map<String, ResolutionResult> preResolvedResults
  )
    throws InjectionFailedException, ReflectiveOperationException, NoSuchAlgorithmException, IOException, URISyntaxException;
}
