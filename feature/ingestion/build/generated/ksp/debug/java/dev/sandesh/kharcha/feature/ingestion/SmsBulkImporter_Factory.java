package dev.sandesh.kharcha.feature.ingestion;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.feature.ingestion.service.IngestionService;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class SmsBulkImporter_Factory implements Factory<SmsBulkImporter> {
  private final Provider<Context> contextProvider;

  private final Provider<IngestionService> ingestionServiceProvider;

  public SmsBulkImporter_Factory(Provider<Context> contextProvider,
      Provider<IngestionService> ingestionServiceProvider) {
    this.contextProvider = contextProvider;
    this.ingestionServiceProvider = ingestionServiceProvider;
  }

  @Override
  public SmsBulkImporter get() {
    return newInstance(contextProvider.get(), ingestionServiceProvider.get());
  }

  public static SmsBulkImporter_Factory create(javax.inject.Provider<Context> contextProvider,
      javax.inject.Provider<IngestionService> ingestionServiceProvider) {
    return new SmsBulkImporter_Factory(Providers.asDaggerProvider(contextProvider), Providers.asDaggerProvider(ingestionServiceProvider));
  }

  public static SmsBulkImporter_Factory create(Provider<Context> contextProvider,
      Provider<IngestionService> ingestionServiceProvider) {
    return new SmsBulkImporter_Factory(contextProvider, ingestionServiceProvider);
  }

  public static SmsBulkImporter newInstance(Context context, IngestionService ingestionService) {
    return new SmsBulkImporter(context, ingestionService);
  }
}
