package dev.sandesh.kharcha.home;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.core.model.BudgetRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<TransactionRepository> transactionRepositoryProvider;

  private final Provider<BudgetRepository> budgetRepositoryProvider;

  private final Provider<CategoryRepository> categoryRepositoryProvider;

  public HomeViewModel_Factory(Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<BudgetRepository> budgetRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider) {
    this.transactionRepositoryProvider = transactionRepositoryProvider;
    this.budgetRepositoryProvider = budgetRepositoryProvider;
    this.categoryRepositoryProvider = categoryRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(transactionRepositoryProvider.get(), budgetRepositoryProvider.get(), categoryRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(
      javax.inject.Provider<TransactionRepository> transactionRepositoryProvider,
      javax.inject.Provider<BudgetRepository> budgetRepositoryProvider,
      javax.inject.Provider<CategoryRepository> categoryRepositoryProvider) {
    return new HomeViewModel_Factory(Providers.asDaggerProvider(transactionRepositoryProvider), Providers.asDaggerProvider(budgetRepositoryProvider), Providers.asDaggerProvider(categoryRepositoryProvider));
  }

  public static HomeViewModel_Factory create(
      Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<BudgetRepository> budgetRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider) {
    return new HomeViewModel_Factory(transactionRepositoryProvider, budgetRepositoryProvider, categoryRepositoryProvider);
  }

  public static HomeViewModel newInstance(TransactionRepository transactionRepository,
      BudgetRepository budgetRepository, CategoryRepository categoryRepository) {
    return new HomeViewModel(transactionRepository, budgetRepository, categoryRepository);
  }
}
