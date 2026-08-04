package dev.sandesh.kharcha.core.database.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.core.database.KharchaDatabase;
import dev.sandesh.kharcha.core.database.dao.BillDao;
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
public final class DatabaseModule_ProvideBillDaoFactory implements Factory<BillDao> {
  private final Provider<KharchaDatabase> dbProvider;

  public DatabaseModule_ProvideBillDaoFactory(Provider<KharchaDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public BillDao get() {
    return provideBillDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideBillDaoFactory create(
      javax.inject.Provider<KharchaDatabase> dbProvider) {
    return new DatabaseModule_ProvideBillDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideBillDaoFactory create(Provider<KharchaDatabase> dbProvider) {
    return new DatabaseModule_ProvideBillDaoFactory(dbProvider);
  }

  public static BillDao provideBillDao(KharchaDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideBillDao(db));
  }
}
