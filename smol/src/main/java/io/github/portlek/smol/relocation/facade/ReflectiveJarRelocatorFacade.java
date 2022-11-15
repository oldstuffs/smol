package io.github.portlek.smol.relocation.facade;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.jetbrains.annotations.NotNull;

public record ReflectiveJarRelocatorFacade(
  @NotNull Object relocator,
  @NotNull Method relocatorRunMethod
)
  implements JarRelocatorFacade {
  @Override
  public void run() throws InvocationTargetException, IllegalAccessException {
    this.relocatorRunMethod.invoke(this.relocator);
  }
}
