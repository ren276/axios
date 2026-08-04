package dev.sandesh.kharcha.core.database.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.core.database.dao.BillDao;
import dev.sandesh.kharcha.core.model.BillRepository;
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
public final class DatabaseModule_ProvideBillRepositoryFactory implements Factory<BillRepository> {
  private final Provider<BillDao> daoProvider;

  public DatabaseModule_ProvideBillRepositoryFactory(Provider<BillDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public BillRepository get() {
    return provideBillRepository(daoProvider.get());
  }

  public static DatabaseModule_ProvideBillRepositoryFactory create(
      javax.inject.Provider<BillDao> daoProvider) {
    return new DatabaseModule_ProvideBillRepositoryFactory(Providers.asDaggerProvider(daoProvider));
  }

  public static DatabaseModule_ProvideBillRepositoryFactory create(Provider<BillDao> daoProvider) {
    return new DatabaseModule_ProvideBillRepositoryFactory(daoProvider);
  }

  public static BillRepository provideBillRepository(BillDao dao) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideBillRepository(dao));
  }
}
