package dev.sandesh.kharcha.feature.settings;

import androidx.work.WorkManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.core.model.TransactionRepository;
import dev.sandesh.kharcha.core.model.UserPreferencesRepository;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<UserPreferencesRepository> userPreferencesRepositoryProvider;

  private final Provider<TransactionRepository> transactionRepositoryProvider;

  private final Provider<WorkManager> workManagerProvider;

  private final Provider<DataExporter> dataExporterProvider;

  public SettingsViewModel_Factory(
      Provider<UserPreferencesRepository> userPreferencesRepositoryProvider,
      Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<WorkManager> workManagerProvider, Provider<DataExporter> dataExporterProvider) {
    this.userPreferencesRepositoryProvider = userPreferencesRepositoryProvider;
    this.transactionRepositoryProvider = transactionRepositoryProvider;
    this.workManagerProvider = workManagerProvider;
    this.dataExporterProvider = dataExporterProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(userPreferencesRepositoryProvider.get(), transactionRepositoryProvider.get(), workManagerProvider.get(), dataExporterProvider.get());
  }

  public static SettingsViewModel_Factory create(
      javax.inject.Provider<UserPreferencesRepository> userPreferencesRepositoryProvider,
      javax.inject.Provider<TransactionRepository> transactionRepositoryProvider,
      javax.inject.Provider<WorkManager> workManagerProvider,
      javax.inject.Provider<DataExporter> dataExporterProvider) {
    return new SettingsViewModel_Factory(Providers.asDaggerProvider(userPreferencesRepositoryProvider), Providers.asDaggerProvider(transactionRepositoryProvider), Providers.asDaggerProvider(workManagerProvider), Providers.asDaggerProvider(dataExporterProvider));
  }

  public static SettingsViewModel_Factory create(
      Provider<UserPreferencesRepository> userPreferencesRepositoryProvider,
      Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<WorkManager> workManagerProvider, Provider<DataExporter> dataExporterProvider) {
    return new SettingsViewModel_Factory(userPreferencesRepositoryProvider, transactionRepositoryProvider, workManagerProvider, dataExporterProvider);
  }

  public static SettingsViewModel newInstance(UserPreferencesRepository userPreferencesRepository,
      TransactionRepository transactionRepository, WorkManager workManager,
      DataExporter dataExporter) {
    return new SettingsViewModel(userPreferencesRepository, transactionRepository, workManager, dataExporter);
  }
}
