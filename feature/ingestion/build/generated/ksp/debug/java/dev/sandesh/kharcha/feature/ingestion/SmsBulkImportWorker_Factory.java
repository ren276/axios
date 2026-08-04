package dev.sandesh.kharcha.feature.ingestion;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.core.model.TransactionRepository;
import dev.sandesh.kharcha.feature.parser.rules.ParserEngine;
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
public final class SmsBulkImportWorker_Factory {
  private final Provider<ParserEngine> parserEngineProvider;

  private final Provider<TransactionRepository> transactionRepositoryProvider;

  public SmsBulkImportWorker_Factory(Provider<ParserEngine> parserEngineProvider,
      Provider<TransactionRepository> transactionRepositoryProvider) {
    this.parserEngineProvider = parserEngineProvider;
    this.transactionRepositoryProvider = transactionRepositoryProvider;
  }

  public SmsBulkImportWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams, parserEngineProvider.get(), transactionRepositoryProvider.get());
  }

  public static SmsBulkImportWorker_Factory create(
      javax.inject.Provider<ParserEngine> parserEngineProvider,
      javax.inject.Provider<TransactionRepository> transactionRepositoryProvider) {
    return new SmsBulkImportWorker_Factory(Providers.asDaggerProvider(parserEngineProvider), Providers.asDaggerProvider(transactionRepositoryProvider));
  }

  public static SmsBulkImportWorker_Factory create(Provider<ParserEngine> parserEngineProvider,
      Provider<TransactionRepository> transactionRepositoryProvider) {
    return new SmsBulkImportWorker_Factory(parserEngineProvider, transactionRepositoryProvider);
  }

  public static SmsBulkImportWorker newInstance(Context appContext, WorkerParameters workerParams,
      ParserEngine parserEngine, TransactionRepository transactionRepository) {
    return new SmsBulkImportWorker(appContext, workerParams, parserEngine, transactionRepository);
  }
}
