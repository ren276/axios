package dev.sandesh.kharcha.feature.ingestion;

import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dev.sandesh.kharcha.feature.ingestion.service.IngestionService;
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
public final class KharchaNotificationListenerService_MembersInjector implements MembersInjector<KharchaNotificationListenerService> {
  private final Provider<IngestionService> ingestionServiceProvider;

  public KharchaNotificationListenerService_MembersInjector(
      Provider<IngestionService> ingestionServiceProvider) {
    this.ingestionServiceProvider = ingestionServiceProvider;
  }

  public static MembersInjector<KharchaNotificationListenerService> create(
      Provider<IngestionService> ingestionServiceProvider) {
    return new KharchaNotificationListenerService_MembersInjector(ingestionServiceProvider);
  }

  public static MembersInjector<KharchaNotificationListenerService> create(
      javax.inject.Provider<IngestionService> ingestionServiceProvider) {
    return new KharchaNotificationListenerService_MembersInjector(Providers.asDaggerProvider(ingestionServiceProvider));
  }

  @Override
  public void injectMembers(KharchaNotificationListenerService instance) {
    injectIngestionService(instance, ingestionServiceProvider.get());
  }

  @InjectedFieldSignature("dev.sandesh.kharcha.feature.ingestion.KharchaNotificationListenerService.ingestionService")
  public static void injectIngestionService(KharchaNotificationListenerService instance,
      IngestionService ingestionService) {
    instance.ingestionService = ingestionService;
  }
}
