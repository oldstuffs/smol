package io.github.portlek.smol.resolver.reader.facade;

import org.jetbrains.annotations.NotNull;

public interface GsonFacadeFactory {
  @NotNull
  GsonFacade createFacade() throws ReflectiveOperationException;
}
