package io.github.portlek.smol.relocation.facade;

import io.github.portlek.smol.relocation.RelocationRule;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public interface JarRelocatorFacadeFactory {
  @NotNull
  JarRelocatorFacade createFacade(
    @NotNull File input,
    @NotNull File output,
    @NotNull Collection<RelocationRule> relocationRules
  )
    throws IllegalAccessException, InstantiationException, InvocationTargetException;
}
