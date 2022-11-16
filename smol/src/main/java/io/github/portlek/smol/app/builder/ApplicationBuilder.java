package io.github.portlek.smol.app.builder;

import io.github.portlek.smol.app.Application;
import io.github.portlek.smol.downloader.DependencyDownloaderFactory;
import io.github.portlek.smol.downloader.DownloadNotifier;
import io.github.portlek.smol.downloader.URLDependencyDownloaderFactory;
import io.github.portlek.smol.downloader.output.DependencyOutputWriterFactory;
import io.github.portlek.smol.downloader.strategy.ChecksumFilePathStrategy;
import io.github.portlek.smol.downloader.strategy.FilePathStrategy;
import io.github.portlek.smol.downloader.verify.ChecksumDependencyVerifierFactory;
import io.github.portlek.smol.downloader.verify.DependencyVerifierFactory;
import io.github.portlek.smol.downloader.verify.FileChecksumCalculator;
import io.github.portlek.smol.downloader.verify.PassthroughDependencyVerifierFactory;
import io.github.portlek.smol.injector.DependencyInjector;
import io.github.portlek.smol.injector.DependencyInjectorFactory;
import io.github.portlek.smol.injector.SimpleDependencyInjectorFactory;
import io.github.portlek.smol.injector.helper.InjectionHelperFactory;
import io.github.portlek.smol.injector.loader.Injectable;
import io.github.portlek.smol.logger.LogDispatcher;
import io.github.portlek.smol.logger.ProcessLogger;
import io.github.portlek.smol.relocation.JarFileRelocatorFactory;
import io.github.portlek.smol.relocation.RelocatorFactory;
import io.github.portlek.smol.relocation.facade.ReflectiveJarRelocatorFacadeFactory;
import io.github.portlek.smol.relocation.helper.RelocationHelperFactory;
import io.github.portlek.smol.relocation.helper.VerifyingRelocationHelperFactory;
import io.github.portlek.smol.relocation.meta.FlatFileMetaMediatorFactory;
import io.github.portlek.smol.resolver.CachingDependencyResolverFactory;
import io.github.portlek.smol.resolver.DependencyResolverFactory;
import io.github.portlek.smol.resolver.data.Repository;
import io.github.portlek.smol.resolver.enquirer.PingingRepositoryEnquirerFactory;
import io.github.portlek.smol.resolver.enquirer.RepositoryEnquirerFactory;
import io.github.portlek.smol.resolver.mirrors.MirrorSelector;
import io.github.portlek.smol.resolver.mirrors.SimpleMirrorSelector;
import io.github.portlek.smol.resolver.pinger.HttpURLPinger;
import io.github.portlek.smol.resolver.reader.dependency.DependencyDataProviderFactory;
import io.github.portlek.smol.resolver.reader.dependency.ExternalDependencyDataProviderFactory;
import io.github.portlek.smol.resolver.reader.dependency.GsonDependencyDataProviderFactory;
import io.github.portlek.smol.resolver.reader.facade.ReflectiveGsonFacadeFactory;
import io.github.portlek.smol.resolver.reader.resolution.GsonPreResolutionDataProviderFactory;
import io.github.portlek.smol.resolver.reader.resolution.PreResolutionDataProviderFactory;
import io.github.portlek.smol.resolver.strategy.MavenChecksumPathResolutionStrategy;
import io.github.portlek.smol.resolver.strategy.MavenPathResolutionStrategy;
import io.github.portlek.smol.resolver.strategy.MavenPomPathResolutionStrategy;
import io.github.portlek.smol.resolver.strategy.MavenSnapshotPathResolutionStrategy;
import io.github.portlek.smol.resolver.strategy.MediatingPathResolutionStrategy;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Setter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ApplicationBuilder {

  private static final Path DEFAULT_DOWNLOAD_DIRECTORY = new File(
    String.format("%s/.smol", System.getProperty("user.home"))
  )
    .toPath();

  @Getter
  @NotNull
  final String applicationName;

  @Nullable
  DependencyDataProviderFactory dataProviderFactory;

  @Nullable
  URL dependencyFileUrl;

  @Nullable
  Path downloadDirectoryPath;

  @Nullable
  DownloadNotifier downloadNotifier;

  @Nullable
  DependencyDownloaderFactory downloaderFactory;

  @Nullable
  RepositoryEnquirerFactory enquirerFactory;

  @Nullable
  DependencyInjectorFactory injectorFactory;

  @Nullable
  ProcessLogger logger;

  @Nullable
  MirrorSelector mirrorSelector;

  @Nullable
  DependencyDataProviderFactory moduleDataProviderFactory;

  @Nullable
  PreResolutionDataProviderFactory preResolutionDataProviderFactory;

  @Nullable
  URL preResolutionFileUrl;

  @Nullable
  RelocationHelperFactory relocationHelperFactory;

  @Nullable
  RelocatorFactory relocatorFactory;

  @Nullable
  DependencyResolverFactory resolverFactory;

  @Nullable
  DependencyVerifierFactory verifierFactory;

  @NotNull
  public static ApplicationBuilder appending(@NotNull final String name) {
    return InjectingApplicationBuilder.createAppending(name);
  }

  @NotNull
  public static ApplicationBuilder injecting(
    @NotNull final String name,
    @NotNull final Injectable injectable
  ) {
    return new InjectingApplicationBuilder(name, injectable);
  }

  @NotNull
  public static ApplicationBuilder isolated(
    @NotNull final String name,
    @NotNull final IsolationConfiguration config,
    @NotNull final Object@NotNull[] args
  ) {
    return new IsolatedApplicationBuilder(name, config, args);
  }

  @NotNull
  public final Application build()
    throws IOException, ReflectiveOperationException, URISyntaxException, NoSuchAlgorithmException {
    final var mediatingLogger = LogDispatcher.getMediatingLogger();
    final var logger = this.getLogger();
    mediatingLogger.addLogger(logger);
    final var result = this.buildApplication();
    mediatingLogger.removeLogger(logger);
    return result;
  }

  @NotNull
  protected final DependencyInjector createInjector()
    throws IOException, URISyntaxException, NoSuchAlgorithmException, ReflectiveOperationException {
    return this.getInjectorFactory()
      .create(
        new InjectionHelperFactory(
          this.getDownloadDirectoryPath(),
          this.getRelocatorFactory(),
          this.getDataProviderFactory(),
          this.getRelocationHelperFactory(),
          this.getInjectorFactory(),
          this.getResolverFactory(),
          this.getEnquirerFactory(),
          this.getDownloaderFactory(),
          this.getVerifierFactory(),
          this.getMirrorSelector()
        )
      );
  }

  @NotNull
  protected final DependencyDataProviderFactory getDataProviderFactory()
    throws URISyntaxException, ReflectiveOperationException, NoSuchAlgorithmException, IOException {
    if (this.dataProviderFactory == null) {
      final var gsonFacadeFactory = ReflectiveGsonFacadeFactory.create(
        this.getDownloadDirectoryPath(),
        Collections.singleton(Repository.central())
      );
      this.dataProviderFactory =
        new GsonDependencyDataProviderFactory(gsonFacadeFactory);
    }
    return this.dataProviderFactory;
  }

  @NotNull
  protected final URL getDependencyFileUrl() {
    if (this.dependencyFileUrl == null) {
      this.dependencyFileUrl =
        this.getClass().getClassLoader().getResource("smol.json");
    }
    return Objects.requireNonNull(
      this.dependencyFileUrl,
      "smol.json not found!"
    );
  }

  @NotNull
  protected final Path getDownloadDirectoryPath() {
    if (this.downloadDirectoryPath == null) {
      this.downloadDirectoryPath =
        ApplicationBuilder.DEFAULT_DOWNLOAD_DIRECTORY;
    }
    return this.downloadDirectoryPath;
  }

  @NotNull
  protected final DownloadNotifier getDownloadNotifier() {
    if (this.downloadNotifier == null) {
      this.downloadNotifier = dependency -> {};
    }
    return this.downloadNotifier;
  }

  @NotNull
  protected final DependencyDownloaderFactory getDownloaderFactory() {
    if (this.downloaderFactory == null) {
      this.downloaderFactory = new URLDependencyDownloaderFactory();
    }
    return this.downloaderFactory;
  }

  @NotNull
  protected final RepositoryEnquirerFactory getEnquirerFactory() {
    if (this.enquirerFactory == null) {
      final var releaseStrategy = new MavenPathResolutionStrategy();
      final var snapshotStrategy = new MavenSnapshotPathResolutionStrategy();
      final var resolutionStrategy = new MediatingPathResolutionStrategy(
        releaseStrategy,
        snapshotStrategy
      );
      final var pomURLCreationStrategy = new MavenPomPathResolutionStrategy();
      final var checksumResolutionStrategy = new MavenChecksumPathResolutionStrategy(
        "SHA-1",
        resolutionStrategy
      );
      final var urlPinger = new HttpURLPinger();
      this.enquirerFactory =
        new PingingRepositoryEnquirerFactory(
          resolutionStrategy,
          checksumResolutionStrategy,
          pomURLCreationStrategy,
          urlPinger
        );
    }
    return this.enquirerFactory;
  }

  @NotNull
  protected final DependencyInjectorFactory getInjectorFactory() {
    if (this.injectorFactory == null) {
      this.injectorFactory =
        new SimpleDependencyInjectorFactory(this.getDownloadNotifier());
    }
    return this.injectorFactory;
  }

  @NotNull
  protected final ProcessLogger getLogger() {
    if (this.logger == null) {
      this.logger = (msg, args) -> {};
    }
    return this.logger;
  }

  @NotNull
  protected final MirrorSelector getMirrorSelector() {
    if (this.mirrorSelector == null) {
      this.mirrorSelector = new SimpleMirrorSelector();
    }
    return this.mirrorSelector;
  }

  @NotNull
  protected final DependencyDataProviderFactory getModuleDataProviderFactory()
    throws URISyntaxException, ReflectiveOperationException, NoSuchAlgorithmException, IOException {
    if (this.moduleDataProviderFactory == null) {
      final var gsonFacadeFactory = ReflectiveGsonFacadeFactory.create(
        this.getDownloadDirectoryPath(),
        Collections.singleton(Repository.central())
      );
      this.moduleDataProviderFactory =
        new ExternalDependencyDataProviderFactory(gsonFacadeFactory);
    }
    return this.moduleDataProviderFactory;
  }

  @NotNull
  protected final PreResolutionDataProviderFactory getPreResolutionDataProviderFactory()
    throws URISyntaxException, ReflectiveOperationException, NoSuchAlgorithmException, IOException {
    if (this.preResolutionDataProviderFactory == null) {
      final var gsonFacadeFactory = ReflectiveGsonFacadeFactory.create(
        this.getDownloadDirectoryPath(),
        Collections.singleton(Repository.central())
      );
      this.preResolutionDataProviderFactory =
        new GsonPreResolutionDataProviderFactory(gsonFacadeFactory);
    }
    return this.preResolutionDataProviderFactory;
  }

  @NotNull
  protected final URL getPreResolutionFileUrl() {
    if (this.preResolutionFileUrl == null) {
      this.preResolutionFileUrl =
        this.getClass().getClassLoader().getResource("smol-resolutions.json");
    }
    return Objects.requireNonNull(
      this.preResolutionFileUrl,
      "smol-resolutions.json file not found!"
    );
  }

  @NotNull
  protected final RelocationHelperFactory getRelocationHelperFactory()
    throws NoSuchAlgorithmException, IOException, URISyntaxException {
    if (this.relocationHelperFactory == null) {
      final var checksumCalculator = new FileChecksumCalculator("SHA-256");
      final var pathStrategy = FilePathStrategy.createRelocationStrategy(
        this.getDownloadDirectoryPath().toFile(),
        this.applicationName()
      );
      final var mediatorFactory = new FlatFileMetaMediatorFactory();
      this.relocationHelperFactory =
        new VerifyingRelocationHelperFactory(
          checksumCalculator,
          pathStrategy,
          mediatorFactory
        );
    }
    return this.relocationHelperFactory;
  }

  @NotNull
  protected final RelocatorFactory getRelocatorFactory()
    throws ReflectiveOperationException, NoSuchAlgorithmException, IOException, URISyntaxException {
    if (this.relocatorFactory == null) {
      final var jarRelocatorFacadeFactory = ReflectiveJarRelocatorFacadeFactory.create(
        this.getDownloadDirectoryPath(),
        Collections.singleton(Repository.central())
      );
      this.relocatorFactory =
        new JarFileRelocatorFactory(jarRelocatorFacadeFactory);
    }
    return this.relocatorFactory;
  }

  @NotNull
  protected final DependencyResolverFactory getResolverFactory() {
    if (this.resolverFactory == null) {
      final var pinger = new HttpURLPinger();
      this.resolverFactory = new CachingDependencyResolverFactory(pinger);
    }
    return this.resolverFactory;
  }

  @NotNull
  protected final DependencyVerifierFactory getVerifierFactory()
    throws NoSuchAlgorithmException {
    if (this.verifierFactory == null) {
      final var filePathStrategy = ChecksumFilePathStrategy.createStrategy(
        this.getDownloadDirectoryPath().toFile(),
        "SHA-1"
      );
      final var checksumOutputFactory = new DependencyOutputWriterFactory(
        filePathStrategy
      );
      final var fallback = new PassthroughDependencyVerifierFactory();
      final var checksumCalculator = new FileChecksumCalculator("SHA-1");
      this.verifierFactory =
        new ChecksumDependencyVerifierFactory(
          checksumOutputFactory,
          fallback,
          checksumCalculator
        );
    }
    return this.verifierFactory;
  }

  @NotNull
  protected abstract Application buildApplication()
    throws IOException, ReflectiveOperationException, URISyntaxException, NoSuchAlgorithmException;
}
