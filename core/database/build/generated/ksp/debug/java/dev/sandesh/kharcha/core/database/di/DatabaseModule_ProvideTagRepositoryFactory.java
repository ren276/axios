package dev.sandesh.kharcha.core.database.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.core.database.dao.TagDao;
import dev.sandesh.kharcha.core.model.TagRepository;
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
public final class DatabaseModule_ProvideTagRepositoryFactory implements Factory<TagRepository> {
  private final Provider<TagDao> daoProvider;

  public DatabaseModule_ProvideTagRepositoryFactory(Provider<TagDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public TagRepository get() {
    return provideTagRepository(daoProvider.get());
  }

  public static DatabaseModule_ProvideTagRepositoryFactory create(
      javax.inject.Provider<TagDao> daoProvider) {
    return new DatabaseModule_ProvideTagRepositoryFactory(Providers.asDaggerProvider(daoProvider));
  }

  public static DatabaseModule_ProvideTagRepositoryFactory create(Provider<TagDao> daoProvider) {
    return new DatabaseModule_ProvideTagRepositoryFactory(daoProvider);
  }

  public static TagRepository provideTagRepository(TagDao dao) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideTagRepository(dao));
  }
}
