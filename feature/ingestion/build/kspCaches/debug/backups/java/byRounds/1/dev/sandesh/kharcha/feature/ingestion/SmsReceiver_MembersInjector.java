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
public final class SmsReceiver_MembersInjector implements MembersInjector<SmsReceiver> {
  private final Provider<IngestionService> ingestionServiceProvider;

  public SmsReceiver_MembersInjector(Provider<IngestionService> ingestionServiceProvider) {
    this.ingestionServiceProvider = ingestionServiceProvider;
  }

  public static MembersInjector<SmsReceiver> create(
      Provider<IngestionService> ingestionServiceProvider) {
    return new SmsReceiver_MembersInjector(ingestionServiceProvider);
  }

  public static MembersInjector<SmsReceiver> create(
      javax.inject.Provider<IngestionService> ingestionServiceProvider) {
    return new SmsReceiver_MembersInjector(Providers.asDaggerProvider(ingestionServiceProvider));
  }

  @Override
  public void injectMembers(SmsReceiver instance) {
    injectIngestionService(instance, ingestionServiceProvider.get());
  }

  @InjectedFieldSignature("dev.sandesh.kharcha.feature.ingestion.SmsReceiver.ingestionService")
  public static void injectIngestionService(SmsReceiver instance,
      IngestionService ingestionService) {
    instance.ingestionService = ingestionService;
  }
}
