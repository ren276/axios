package dev.sandesh.kharcha.core.database.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.core.database.dao.AccountDao;
import dev.sandesh.kharcha.core.model.AccountRepository;
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
public final class DatabaseModule_ProvideAccountRepositoryFactory implements Factory<AccountRepository> {
  private final Provider<AccountDao> daoProvider;

  public DatabaseModule_ProvideAccountRepositoryFactory(Provider<AccountDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public AccountRepository get() {
    return provideAccountRepository(daoProvider.get());
  }

  public static DatabaseModule_ProvideAccountRepositoryFactory create(
      javax.inject.Provider<AccountDao> daoProvider) {
    return new DatabaseModule_ProvideAccountRepositoryFactory(Providers.asDaggerProvider(daoProvider));
  }

  public static DatabaseModule_ProvideAccountRepositoryFactory create(
      Provider<AccountDao> daoProvider) {
    return new DatabaseModule_ProvideAccountRepositoryFactory(daoProvider);
  }

  public static AccountRepository provideAccountRepository(AccountDao dao) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideAccountRepository(dao));
  }
}
