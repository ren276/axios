package dev.sandesh.kharcha.feature.ingestion.service;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.core.model.AccountRepository;
import dev.sandesh.kharcha.core.model.TransactionRepository;
import dev.sandesh.kharcha.feature.llm.LlmProvider;
import dev.sandesh.kharcha.feature.parser.rules.ParserEngine;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class IngestionService_Factory implements Factory<IngestionService> {
  private final Provider<ParserEngine> parserEngineProvider;

  private final Provider<TransactionRepository> transactionRepositoryProvider;

  private final Provider<AccountRepository> accountRepositoryProvider;

  private final Provider<LlmProvider> llmProvider;

  public IngestionService_Factory(Provider<ParserEngine> parserEngineProvider,
      Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<AccountRepository> accountRepositoryProvider, Provider<LlmProvider> llmProvider) {
    this.parserEngineProvider = parserEngineProvider;
    this.transactionRepositoryProvider = transactionRepositoryProvider;
    this.accountRepositoryProvider = accountRepositoryProvider;
    this.llmProvider = llmProvider;
  }

  @Override
  public IngestionService get() {
    return newInstance(parserEngineProvider.get(), transactionRepositoryProvider.get(), accountRepositoryProvider.get(), llmProvider.get());
  }

  public static IngestionService_Factory create(
      javax.inject.Provider<ParserEngine> parserEngineProvider,
      javax.inject.Provider<TransactionRepository> transactionRepositoryProvider,
      javax.inject.Provider<AccountRepository> accountRepositoryProvider,
      javax.inject.Provider<LlmProvider> llmProvider) {
    return new IngestionService_Factory(Providers.asDaggerProvider(parserEngineProvider), Providers.asDaggerProvider(transactionRepositoryProvider), Providers.asDaggerProvider(accountRepositoryProvider), Providers.asDaggerProvider(llmProvider));
  }

  public static IngestionService_Factory create(Provider<ParserEngine> parserEngineProvider,
      Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<AccountRepository> accountRepositoryProvider, Provider<LlmProvider> llmProvider) {
    return new IngestionService_Factory(parserEngineProvider, transactionRepositoryProvider, accountRepositoryProvider, llmProvider);
  }

  public static IngestionService newInstance(ParserEngine parserEngine,
      TransactionRepository transactionRepository, AccountRepository accountRepository,
      LlmProvider llmProvider) {
    return new IngestionService(parserEngine, transactionRepository, accountRepository, llmProvider);
  }
}
