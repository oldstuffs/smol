package io.github.portlek.smol.injector;

import io.github.portlek.smol.downloader.DownloadNotifier;
import io.github.portlek.smol.injector.helper.InjectionHelper;
import io.github.portlek.smol.injector.helper.InjectionHelperFactory;
import io.github.portlek.smol.injector.loader.Injectable;
import io.github.portlek.smol.resolver.ResolutionResult;
import io.github.portlek.smol.resolver.data.Dependency;
import io.github.portlek.smol.resolver.data.DependencyData;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public record SimpleDependencyInjector(
  @NotNull InjectionHelperFactory injectionHelperFactory,
  @NotNull DownloadNotifier downloadNotifier
)
  implements DependencyInjector {
  @Override
  public void inject(
    @NotNull final Injectable injectable,
    @NotNull final DependencyData data,
    @NotNull final Map<String, ResolutionResult> preResolvedResults
  )
    throws ReflectiveOperationException, NoSuchAlgorithmException, IOException, URISyntaxException {
    final var helper =
      this.injectionHelperFactory.create(data, preResolvedResults);
    this.injectDependencies(injectable, helper, data.dependencies());
  }

  private void injectDependencies(
    @NotNull final Injectable injectable,
    @NotNull final InjectionHelper injectionHelper,
    @NotNull final Collection<Dependency> dependencies
  ) throws ReflectiveOperationException {
    for (final var dependency : dependencies) {
      try {
        final var depJar = injectionHelper.fetch(dependency);
        if (depJar == null) {
          continue;
        }
        this.downloadNotifier.accept(dependency);
        injectable.inject(depJar.toURI().toURL());
        this.injectDependencies(
            injectable,
            injectionHelper,
            dependency.transitive()
          );
      } catch (final IOException e) {
        throw new InjectionFailedException(dependency, e);
      } catch (
        final IllegalAccessException
        | InvocationTargetException
        | URISyntaxException e
      ) {
        e.printStackTrace();
      }
    }
  }
}
