package dev.sandesh.kharcha;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class KharchaApplication_MembersInjector implements MembersInjector<KharchaApplication> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public KharchaApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<KharchaApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new KharchaApplication_MembersInjector(workerFactoryProvider);
  }

  public static MembersInjector<KharchaApplication> create(
      javax.inject.Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new KharchaApplication_MembersInjector(Providers.asDaggerProvider(workerFactoryProvider));
  }

  @Override
  public void injectMembers(KharchaApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("dev.sandesh.kharcha.KharchaApplication.workerFactory")
  public static void injectWorkerFactory(KharchaApplication instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
