package dev.sandesh.kharcha.core.database.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.core.database.dao.TagDao;
import dev.sandesh.kharcha.core.database.dao.TransactionDao;
import dev.sandesh.kharcha.core.model.TransactionRepository;
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
public final class DatabaseModule_ProvideTransactionRepositoryFactory implements Factory<TransactionRepository> {
  private final Provider<TransactionDao> daoProvider;

  private final Provider<TagDao> tagDaoProvider;

  public DatabaseModule_ProvideTransactionRepositoryFactory(Provider<TransactionDao> daoProvider,
      Provider<TagDao> tagDaoProvider) {
    this.daoProvider = daoProvider;
    this.tagDaoProvider = tagDaoProvider;
  }

  @Override
  public TransactionRepository get() {
    return provideTransactionRepository(daoProvider.get(), tagDaoProvider.get());
  }

  public static DatabaseModule_ProvideTransactionRepositoryFactory create(
      javax.inject.Provider<TransactionDao> daoProvider,
      javax.inject.Provider<TagDao> tagDaoProvider) {
    return new DatabaseModule_ProvideTransactionRepositoryFactory(Providers.asDaggerProvider(daoProvider), Providers.asDaggerProvider(tagDaoProvider));
  }

  public static DatabaseModule_ProvideTransactionRepositoryFactory create(
      Provider<TransactionDao> daoProvider, Provider<TagDao> tagDaoProvider) {
    return new DatabaseModule_ProvideTransactionRepositoryFactory(daoProvider, tagDaoProvider);
  }

  public static TransactionRepository provideTransactionRepository(TransactionDao dao,
      TagDao tagDao) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideTransactionRepository(dao, tagDao));
  }
}
