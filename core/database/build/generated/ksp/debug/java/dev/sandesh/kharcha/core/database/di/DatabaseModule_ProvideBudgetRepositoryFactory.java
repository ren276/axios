package dev.sandesh.kharcha.core.database.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.core.database.dao.BudgetDao;
import dev.sandesh.kharcha.core.model.BudgetRepository;
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
public final class DatabaseModule_ProvideBudgetRepositoryFactory implements Factory<BudgetRepository> {
  private final Provider<BudgetDao> daoProvider;

  public DatabaseModule_ProvideBudgetRepositoryFactory(Provider<BudgetDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public BudgetRepository get() {
    return provideBudgetRepository(daoProvider.get());
  }

  public static DatabaseModule_ProvideBudgetRepositoryFactory create(
      javax.inject.Provider<BudgetDao> daoProvider) {
    return new DatabaseModule_ProvideBudgetRepositoryFactory(Providers.asDaggerProvider(daoProvider));
  }

  public static DatabaseModule_ProvideBudgetRepositoryFactory create(
      Provider<BudgetDao> daoProvider) {
    return new DatabaseModule_ProvideBudgetRepositoryFactory(daoProvider);
  }

  public static BudgetRepository provideBudgetRepository(BudgetDao dao) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideBudgetRepository(dao));
  }
}
