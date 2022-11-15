package io.github.portlek.smol.relocation.facade;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface JarRelocatorFacade {
  void run()
    throws IOException, InvocationTargetException, IllegalAccessException;
}
