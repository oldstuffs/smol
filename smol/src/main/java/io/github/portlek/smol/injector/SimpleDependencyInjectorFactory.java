package io.github.portlek.smol.injector;

import io.github.portlek.smol.injector.helper.InjectionHelperFactory;
import org.jetbrains.annotations.NotNull;

public final class SimpleDependencyInjectorFactory
  implements DependencyInjectorFactory {

  @NotNull
  @Override
  public DependencyInjector create(
    @NotNull final InjectionHelperFactory injectionHelperFactory
  ) {
    return new SimpleDependencyInjector(injectionHelperFactory);
  }
}
