package io.github.portlek.smol.relocation.helper;

import io.github.portlek.smol.relocation.Relocator;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface RelocationHelperFactory {
  @NotNull
  RelocationHelper create(@NotNull Relocator relocator)
    throws NoSuchAlgorithmException, IOException, URISyntaxException;
}
