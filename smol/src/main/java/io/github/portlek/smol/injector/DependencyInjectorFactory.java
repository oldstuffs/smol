package io.github.portlek.smol.injector;

import io.github.portlek.smol.injector.helper.InjectionHelperFactory;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface DependencyInjectorFactory {
  @NotNull
  DependencyInjector create(
    @NotNull InjectionHelperFactory injectionHelperFactory
  );
}
