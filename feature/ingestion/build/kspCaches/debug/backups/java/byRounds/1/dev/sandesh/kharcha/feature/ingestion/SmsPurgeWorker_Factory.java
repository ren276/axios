package dev.sandesh.kharcha.feature.ingestion;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.core.model.TransactionRepository;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class SmsPurgeWorker_Factory {
  private final Provider<TransactionRepository> transactionRepositoryProvider;

  public SmsPurgeWorker_Factory(Provider<TransactionRepository> transactionRepositoryProvider) {
    this.transactionRepositoryProvider = transactionRepositoryProvider;
  }

  public SmsPurgeWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams, transactionRepositoryProvider.get());
  }

  public static SmsPurgeWorker_Factory create(
      javax.inject.Provider<TransactionRepository> transactionRepositoryProvider) {
    return new SmsPurgeWorker_Factory(Providers.asDaggerProvider(transactionRepositoryProvider));
  }

  public static SmsPurgeWorker_Factory create(
      Provider<TransactionRepository> transactionRepositoryProvider) {
    return new SmsPurgeWorker_Factory(transactionRepositoryProvider);
  }

  public static SmsPurgeWorker newInstance(Context appContext, WorkerParameters workerParams,
      TransactionRepository transactionRepository) {
    return new SmsPurgeWorker(appContext, workerParams, transactionRepository);
  }
}
