package dev.sandesh.kharcha.feature.accounts;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.core.model.AccountRepository;
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
public final class AccountsDashboardViewModel_Factory implements Factory<AccountsDashboardViewModel> {
  private final Provider<AccountRepository> accountRepositoryProvider;

  public AccountsDashboardViewModel_Factory(Provider<AccountRepository> accountRepositoryProvider) {
    this.accountRepositoryProvider = accountRepositoryProvider;
  }

  @Override
  public AccountsDashboardViewModel get() {
    return newInstance(accountRepositoryProvider.get());
  }

  public static AccountsDashboardViewModel_Factory create(
      javax.inject.Provider<AccountRepository> accountRepositoryProvider) {
    return new AccountsDashboardViewModel_Factory(Providers.asDaggerProvider(accountRepositoryProvider));
  }

  public static AccountsDashboardViewModel_Factory create(
      Provider<AccountRepository> accountRepositoryProvider) {
    return new AccountsDashboardViewModel_Factory(accountRepositoryProvider);
  }

  public static AccountsDashboardViewModel newInstance(AccountRepository accountRepository) {
    return new AccountsDashboardViewModel(accountRepository);
  }
}
