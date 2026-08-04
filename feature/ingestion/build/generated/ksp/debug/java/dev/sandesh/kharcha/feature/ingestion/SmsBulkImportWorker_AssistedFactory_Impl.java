package dev.sandesh.kharcha.feature.ingestion;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class SmsBulkImportWorker_AssistedFactory_Impl implements SmsBulkImportWorker_AssistedFactory {
  private final SmsBulkImportWorker_Factory delegateFactory;

  SmsBulkImportWorker_AssistedFactory_Impl(SmsBulkImportWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public SmsBulkImportWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<SmsBulkImportWorker_AssistedFactory> create(
      SmsBulkImportWorker_Factory delegateFactory) {
    return InstanceFactory.create(new SmsBulkImportWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<SmsBulkImportWorker_AssistedFactory> createFactoryProvider(
      SmsBulkImportWorker_Factory delegateFactory) {
    return InstanceFactory.create(new SmsBulkImportWorker_AssistedFactory_Impl(delegateFactory));
  }
}
