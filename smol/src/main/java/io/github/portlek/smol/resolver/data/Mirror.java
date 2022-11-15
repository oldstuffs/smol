package io.github.portlek.smol.resolver.data;

import java.net.URL;
import org.jetbrains.annotations.NotNull;

public record Mirror(@NotNull URL mirroring, @NotNull URL original) {}
