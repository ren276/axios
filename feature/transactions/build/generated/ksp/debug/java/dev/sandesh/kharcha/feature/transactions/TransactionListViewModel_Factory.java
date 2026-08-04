package dev.sandesh.kharcha.feature.transactions;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.core.model.CategoryRepository;
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
public final class TransactionListViewModel_Factory implements Factory<TransactionListViewModel> {
  private final Provider<TransactionRepository> transactionRepositoryProvider;

  private final Provider<CategoryRepository> categoryRepositoryProvider;

  public TransactionListViewModel_Factory(
      Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider) {
    this.transactionRepositoryProvider = transactionRepositoryProvider;
    this.categoryRepositoryProvider = categoryRepositoryProvider;
  }

  @Override
  public TransactionListViewModel get() {
    return newInstance(transactionRepositoryProvider.get(), categoryRepositoryProvider.get());
  }

  public static TransactionListViewModel_Factory create(
      javax.inject.Provider<TransactionRepository> transactionRepositoryProvider,
      javax.inject.Provider<CategoryRepository> categoryRepositoryProvider) {
    return new TransactionListViewModel_Factory(Providers.asDaggerProvider(transactionRepositoryProvider), Providers.asDaggerProvider(categoryRepositoryProvider));
  }

  public static TransactionListViewModel_Factory create(
      Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider) {
    return new TransactionListViewModel_Factory(transactionRepositoryProvider, categoryRepositoryProvider);
  }

  public static TransactionListViewModel newInstance(TransactionRepository transactionRepository,
      CategoryRepository categoryRepository) {
    return new TransactionListViewModel(transactionRepository, categoryRepository);
  }
}
