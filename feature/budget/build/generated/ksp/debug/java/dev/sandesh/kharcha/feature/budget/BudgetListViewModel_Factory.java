package dev.sandesh.kharcha.feature.budget;

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
public final class BudgetListViewModel_Factory implements Factory<BudgetListViewModel> {
  private final Provider<BudgetRepository> budgetRepositoryProvider;

  private final Provider<TransactionRepository> transactionRepositoryProvider;

  private final Provider<CategoryRepository> categoryRepositoryProvider;

  public BudgetListViewModel_Factory(Provider<BudgetRepository> budgetRepositoryProvider,
      Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider) {
    this.budgetRepositoryProvider = budgetRepositoryProvider;
    this.transactionRepositoryProvider = transactionRepositoryProvider;
    this.categoryRepositoryProvider = categoryRepositoryProvider;
  }

  @Override
  public BudgetListViewModel get() {
    return newInstance(budgetRepositoryProvider.get(), transactionRepositoryProvider.get(), categoryRepositoryProvider.get());
  }

  public static BudgetListViewModel_Factory create(
      javax.inject.Provider<BudgetRepository> budgetRepositoryProvider,
      javax.inject.Provider<TransactionRepository> transactionRepositoryProvider,
      javax.inject.Provider<CategoryRepository> categoryRepositoryProvider) {
    return new BudgetListViewModel_Factory(Providers.asDaggerProvider(budgetRepositoryProvider), Providers.asDaggerProvider(transactionRepositoryProvider), Providers.asDaggerProvider(categoryRepositoryProvider));
  }

  public static BudgetListViewModel_Factory create(
      Provider<BudgetRepository> budgetRepositoryProvider,
      Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider) {
    return new BudgetListViewModel_Factory(budgetRepositoryProvider, transactionRepositoryProvider, categoryRepositoryProvider);
  }

  public static BudgetListViewModel newInstance(BudgetRepository budgetRepository,
      TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
    return new BudgetListViewModel(budgetRepository, transactionRepository, categoryRepository);
  }
}
