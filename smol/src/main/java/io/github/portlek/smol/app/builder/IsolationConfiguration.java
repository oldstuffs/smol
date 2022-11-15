package io.github.portlek.smol.app.builder;

import io.github.portlek.smol.app.module.ModuleExtractor;
import io.github.portlek.smol.app.module.TemporaryModuleExtractor;
import io.github.portlek.smol.misc.Modules;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record IsolationConfiguration(
  @NotNull String applicationClass,
  @NotNull Collection<String> modules,
  @NotNull ClassLoader parentClassloader,
  @NotNull ModuleExtractor moduleExtractor
) {
  @NotNull
  public static Builder builder(@NotNull final String applicationClass) {
    return IsolationConfiguration.builder().applicationClass(applicationClass);
  }

  @NotNull
  public static Builder builder() {
    return new Builder();
  }

  @Accessors(fluent = true)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static final class Builder {

    @Setter
    @Nullable
    private String applicationClass;

    @Setter
    @Nullable
    private ModuleExtractor moduleExtractor;

    @Setter
    private Collection<String> modules = new HashSet<>();

    @Setter
    @Nullable
    private ClassLoader parentClassloader;

    @NotNull
    public IsolationConfiguration build()
      throws IOException, URISyntaxException {
      if (this.applicationClass == null) {
        throw new AssertionError("Application Class not Provided!");
      }
      if (this.modules == null || this.modules.isEmpty()) {
        this.modules = Modules.findLocalModules();
      }
      if (this.moduleExtractor == null) {
        this.moduleExtractor = new TemporaryModuleExtractor();
      }
      if (this.parentClassloader == null) {
        this.parentClassloader = ClassLoader.getSystemClassLoader().getParent();
      }
      return new IsolationConfiguration(
        this.applicationClass,
        this.modules,
        this.parentClassloader,
        this.moduleExtractor
      );
    }

    @NotNull
    public Builder module(@NotNull final String module) {
      this.modules.add(module);
      return this;
    }
  }
}
