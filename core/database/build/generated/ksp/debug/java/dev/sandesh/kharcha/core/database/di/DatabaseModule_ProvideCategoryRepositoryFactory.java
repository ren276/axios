package dev.sandesh.kharcha.core.database.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.core.database.dao.CategoryDao;
import dev.sandesh.kharcha.core.model.CategoryRepository;
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
public final class DatabaseModule_ProvideCategoryRepositoryFactory implements Factory<CategoryRepository> {
  private final Provider<CategoryDao> daoProvider;

  public DatabaseModule_ProvideCategoryRepositoryFactory(Provider<CategoryDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public CategoryRepository get() {
    return provideCategoryRepository(daoProvider.get());
  }

  public static DatabaseModule_ProvideCategoryRepositoryFactory create(
      javax.inject.Provider<CategoryDao> daoProvider) {
    return new DatabaseModule_ProvideCategoryRepositoryFactory(Providers.asDaggerProvider(daoProvider));
  }

  public static DatabaseModule_ProvideCategoryRepositoryFactory create(
      Provider<CategoryDao> daoProvider) {
    return new DatabaseModule_ProvideCategoryRepositoryFactory(daoProvider);
  }

  public static CategoryRepository provideCategoryRepository(CategoryDao dao) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideCategoryRepository(dao));
  }
}
