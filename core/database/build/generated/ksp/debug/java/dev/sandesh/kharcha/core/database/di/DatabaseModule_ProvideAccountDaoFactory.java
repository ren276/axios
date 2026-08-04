package dev.sandesh.kharcha.core.database.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.core.database.KharchaDatabase;
import dev.sandesh.kharcha.core.database.dao.AccountDao;
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
public final class DatabaseModule_ProvideAccountDaoFactory implements Factory<AccountDao> {
  private final Provider<KharchaDatabase> dbProvider;

  public DatabaseModule_ProvideAccountDaoFactory(Provider<KharchaDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public AccountDao get() {
    return provideAccountDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideAccountDaoFactory create(
      javax.inject.Provider<KharchaDatabase> dbProvider) {
    return new DatabaseModule_ProvideAccountDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideAccountDaoFactory create(
      Provider<KharchaDatabase> dbProvider) {
    return new DatabaseModule_ProvideAccountDaoFactory(dbProvider);
  }

  public static AccountDao provideAccountDao(KharchaDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideAccountDao(db));
  }
}
