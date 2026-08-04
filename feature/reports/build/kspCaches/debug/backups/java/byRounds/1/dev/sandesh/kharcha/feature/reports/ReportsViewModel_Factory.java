package dev.sandesh.kharcha.feature.reports;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.core.model.CategoryRepository;
import dev.sandesh.kharcha.core.model.TransactionRepository;
import dev.sandesh.kharcha.feature.llm.LlmProvider;
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
public final class ReportsViewModel_Factory implements Factory<ReportsViewModel> {
  private final Provider<TransactionRepository> transactionRepositoryProvider;

  private final Provider<CategoryRepository> categoryRepositoryProvider;

  private final Provider<LlmProvider> llmProvider;

  public ReportsViewModel_Factory(Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider, Provider<LlmProvider> llmProvider) {
    this.transactionRepositoryProvider = transactionRepositoryProvider;
    this.categoryRepositoryProvider = categoryRepositoryProvider;
    this.llmProvider = llmProvider;
  }

  @Override
  public ReportsViewModel get() {
    return newInstance(transactionRepositoryProvider.get(), categoryRepositoryProvider.get(), llmProvider.get());
  }

  public static ReportsViewModel_Factory create(
      javax.inject.Provider<TransactionRepository> transactionRepositoryProvider,
      javax.inject.Provider<CategoryRepository> categoryRepositoryProvider,
      javax.inject.Provider<LlmProvider> llmProvider) {
    return new ReportsViewModel_Factory(Providers.asDaggerProvider(transactionRepositoryProvider), Providers.asDaggerProvider(categoryRepositoryProvider), Providers.asDaggerProvider(llmProvider));
  }

  public static ReportsViewModel_Factory create(
      Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider, Provider<LlmProvider> llmProvider) {
    return new ReportsViewModel_Factory(transactionRepositoryProvider, categoryRepositoryProvider, llmProvider);
  }

  public static ReportsViewModel newInstance(TransactionRepository transactionRepository,
      CategoryRepository categoryRepository, LlmProvider llmProvider) {
    return new ReportsViewModel(transactionRepository, categoryRepository, llmProvider);
  }
}
