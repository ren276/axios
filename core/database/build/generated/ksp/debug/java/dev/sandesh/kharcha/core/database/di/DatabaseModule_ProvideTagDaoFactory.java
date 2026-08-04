package dev.sandesh.kharcha.core.database.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.core.database.KharchaDatabase;
import dev.sandesh.kharcha.core.database.dao.TagDao;
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
public final class DatabaseModule_ProvideTagDaoFactory implements Factory<TagDao> {
  private final Provider<KharchaDatabase> dbProvider;

  public DatabaseModule_ProvideTagDaoFactory(Provider<KharchaDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public TagDao get() {
    return provideTagDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideTagDaoFactory create(
      javax.inject.Provider<KharchaDatabase> dbProvider) {
    return new DatabaseModule_ProvideTagDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideTagDaoFactory create(Provider<KharchaDatabase> dbProvider) {
    return new DatabaseModule_ProvideTagDaoFactory(dbProvider);
  }

  public static TagDao provideTagDao(KharchaDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideTagDao(db));
  }
}
