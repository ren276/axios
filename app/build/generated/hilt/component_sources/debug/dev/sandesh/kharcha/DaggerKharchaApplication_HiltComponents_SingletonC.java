package dev.sandesh.kharcha;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkManager;
import androidx.work.WorkerParameters;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SingleCheck;
import dev.sandesh.kharcha.app.di.AppModule_ProvideParserEngineFactory;
import dev.sandesh.kharcha.app.di.AppModule_ProvideUserPreferencesRepositoryFactory;
import dev.sandesh.kharcha.app.di.AppModule_ProvideWorkManagerFactory;
import dev.sandesh.kharcha.core.database.KharchaDatabase;
import dev.sandesh.kharcha.core.database.dao.AccountDao;
import dev.sandesh.kharcha.core.database.dao.BillDao;
import dev.sandesh.kharcha.core.database.dao.BudgetDao;
import dev.sandesh.kharcha.core.database.dao.CategoryDao;
import dev.sandesh.kharcha.core.database.dao.TagDao;
import dev.sandesh.kharcha.core.database.dao.TransactionDao;
import dev.sandesh.kharcha.core.database.di.DatabaseModule_ProvideAccountDaoFactory;
import dev.sandesh.kharcha.core.database.di.DatabaseModule_ProvideAccountRepositoryFactory;
import dev.sandesh.kharcha.core.database.di.DatabaseModule_ProvideBillDaoFactory;
import dev.sandesh.kharcha.core.database.di.DatabaseModule_ProvideBillRepositoryFactory;
import dev.sandesh.kharcha.core.database.di.DatabaseModule_ProvideBudgetDaoFactory;
import dev.sandesh.kharcha.core.database.di.DatabaseModule_ProvideBudgetRepositoryFactory;
import dev.sandesh.kharcha.core.database.di.DatabaseModule_ProvideCategoryDaoFactory;
import dev.sandesh.kharcha.core.database.di.DatabaseModule_ProvideCategoryRepositoryFactory;
import dev.sandesh.kharcha.core.database.di.DatabaseModule_ProvideDatabaseFactory;
import dev.sandesh.kharcha.core.database.di.DatabaseModule_ProvideTagDaoFactory;
import dev.sandesh.kharcha.core.database.di.DatabaseModule_ProvideTransactionDaoFactory;
import dev.sandesh.kharcha.core.database.di.DatabaseModule_ProvideTransactionRepositoryFactory;
import dev.sandesh.kharcha.core.model.AccountRepository;
import dev.sandesh.kharcha.core.model.BillRepository;
import dev.sandesh.kharcha.core.model.BudgetRepository;
import dev.sandesh.kharcha.core.model.CategoryRepository;
import dev.sandesh.kharcha.core.model.TransactionRepository;
import dev.sandesh.kharcha.core.model.UserPreferencesRepository;
import dev.sandesh.kharcha.feature.accounts.AccountsDashboardViewModel;
import dev.sandesh.kharcha.feature.accounts.AccountsDashboardViewModel_HiltModules;
import dev.sandesh.kharcha.feature.accounts.AccountsDashboardViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import dev.sandesh.kharcha.feature.accounts.AccountsDashboardViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dev.sandesh.kharcha.feature.budget.BudgetListViewModel;
import dev.sandesh.kharcha.feature.budget.BudgetListViewModel_HiltModules;
import dev.sandesh.kharcha.feature.budget.BudgetListViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import dev.sandesh.kharcha.feature.budget.BudgetListViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dev.sandesh.kharcha.feature.budget.bills.BillReminderWorker;
import dev.sandesh.kharcha.feature.budget.bills.BillReminderWorker_AssistedFactory;
import dev.sandesh.kharcha.feature.ingestion.KharchaNotificationListenerService;
import dev.sandesh.kharcha.feature.ingestion.KharchaNotificationListenerService_MembersInjector;
import dev.sandesh.kharcha.feature.ingestion.SmsBulkImportWorker;
import dev.sandesh.kharcha.feature.ingestion.SmsBulkImportWorker_AssistedFactory;
import dev.sandesh.kharcha.feature.ingestion.SmsPurgeWorker;
import dev.sandesh.kharcha.feature.ingestion.SmsPurgeWorker_AssistedFactory;
import dev.sandesh.kharcha.feature.ingestion.SmsReceiver;
import dev.sandesh.kharcha.feature.ingestion.SmsReceiver_MembersInjector;
import dev.sandesh.kharcha.feature.ingestion.service.IngestionService;
import dev.sandesh.kharcha.feature.llm.JsonSchemaValidator;
import dev.sandesh.kharcha.feature.llm.LlmProvider;
import dev.sandesh.kharcha.feature.llm.OnDeviceGemmaProvider;
import dev.sandesh.kharcha.feature.llm.PromptTemplates;
import dev.sandesh.kharcha.feature.llm.di.LlmModule_ProvideLlmProviderFactory;
import dev.sandesh.kharcha.feature.onboarding.OnboardingViewModel;
import dev.sandesh.kharcha.feature.onboarding.OnboardingViewModel_HiltModules;
import dev.sandesh.kharcha.feature.onboarding.OnboardingViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import dev.sandesh.kharcha.feature.onboarding.OnboardingViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dev.sandesh.kharcha.feature.parser.rules.ParserEngine;
import dev.sandesh.kharcha.feature.reports.ReportsViewModel;
import dev.sandesh.kharcha.feature.reports.ReportsViewModel_HiltModules;
import dev.sandesh.kharcha.feature.reports.ReportsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import dev.sandesh.kharcha.feature.reports.ReportsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dev.sandesh.kharcha.feature.settings.DataExporter;
import dev.sandesh.kharcha.feature.settings.SettingsViewModel;
import dev.sandesh.kharcha.feature.settings.SettingsViewModel_HiltModules;
import dev.sandesh.kharcha.feature.settings.SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import dev.sandesh.kharcha.feature.settings.SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dev.sandesh.kharcha.feature.transactions.ManualEntryViewModel;
import dev.sandesh.kharcha.feature.transactions.ManualEntryViewModel_HiltModules;
import dev.sandesh.kharcha.feature.transactions.ManualEntryViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import dev.sandesh.kharcha.feature.transactions.ManualEntryViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dev.sandesh.kharcha.feature.transactions.TransactionListViewModel;
import dev.sandesh.kharcha.feature.transactions.TransactionListViewModel_HiltModules;
import dev.sandesh.kharcha.feature.transactions.TransactionListViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import dev.sandesh.kharcha.feature.transactions.TransactionListViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dev.sandesh.kharcha.home.HomeViewModel;
import dev.sandesh.kharcha.home.HomeViewModel_HiltModules;
import dev.sandesh.kharcha.home.HomeViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import dev.sandesh.kharcha.home.HomeViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dev.sandesh.kharcha.security.AppLockManager;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerKharchaApplication_HiltComponents_SingletonC {
  private DaggerKharchaApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public KharchaApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements KharchaApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public KharchaApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements KharchaApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public KharchaApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements KharchaApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public KharchaApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements KharchaApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public KharchaApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements KharchaApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public KharchaApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements KharchaApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public KharchaApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements KharchaApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public KharchaApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends KharchaApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends KharchaApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends KharchaApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends KharchaApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(8).put(AccountsDashboardViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AccountsDashboardViewModel_HiltModules.KeyModule.provide()).put(BudgetListViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, BudgetListViewModel_HiltModules.KeyModule.provide()).put(HomeViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, HomeViewModel_HiltModules.KeyModule.provide()).put(ManualEntryViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ManualEntryViewModel_HiltModules.KeyModule.provide()).put(OnboardingViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, OnboardingViewModel_HiltModules.KeyModule.provide()).put(ReportsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ReportsViewModel_HiltModules.KeyModule.provide()).put(SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SettingsViewModel_HiltModules.KeyModule.provide()).put(TransactionListViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, TransactionListViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public void injectMainActivity(MainActivity arg0) {
      injectMainActivity2(arg0);
    }

    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectAppLockManager(instance, singletonCImpl.appLockManagerProvider.get());
      return instance;
    }
  }

  private static final class ViewModelCImpl extends KharchaApplication_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AccountsDashboardViewModel> accountsDashboardViewModelProvider;

    private Provider<BudgetListViewModel> budgetListViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<ManualEntryViewModel> manualEntryViewModelProvider;

    private Provider<OnboardingViewModel> onboardingViewModelProvider;

    private Provider<ReportsViewModel> reportsViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<TransactionListViewModel> transactionListViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private DataExporter dataExporter() {
      return new DataExporter(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.accountsDashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.budgetListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.manualEntryViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.onboardingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.reportsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.transactionListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(8).put(AccountsDashboardViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) accountsDashboardViewModelProvider)).put(BudgetListViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) budgetListViewModelProvider)).put(HomeViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) homeViewModelProvider)).put(ManualEntryViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) manualEntryViewModelProvider)).put(OnboardingViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) onboardingViewModelProvider)).put(ReportsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) reportsViewModelProvider)).put(SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) settingsViewModelProvider)).put(TransactionListViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) transactionListViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dev.sandesh.kharcha.feature.accounts.AccountsDashboardViewModel 
          return (T) new AccountsDashboardViewModel(singletonCImpl.provideAccountRepositoryProvider.get());

          case 1: // dev.sandesh.kharcha.feature.budget.BudgetListViewModel 
          return (T) new BudgetListViewModel(singletonCImpl.provideBudgetRepositoryProvider.get(), singletonCImpl.provideTransactionRepositoryProvider.get(), singletonCImpl.provideCategoryRepositoryProvider.get());

          case 2: // dev.sandesh.kharcha.home.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.provideTransactionRepositoryProvider.get(), singletonCImpl.provideBudgetRepositoryProvider.get(), singletonCImpl.provideCategoryRepositoryProvider.get());

          case 3: // dev.sandesh.kharcha.feature.transactions.ManualEntryViewModel 
          return (T) new ManualEntryViewModel(singletonCImpl.provideTransactionRepositoryProvider.get(), singletonCImpl.provideCategoryRepositoryProvider.get());

          case 4: // dev.sandesh.kharcha.feature.onboarding.OnboardingViewModel 
          return (T) new OnboardingViewModel(singletonCImpl.provideUserPreferencesRepositoryProvider.get());

          case 5: // dev.sandesh.kharcha.feature.reports.ReportsViewModel 
          return (T) new ReportsViewModel(singletonCImpl.provideTransactionRepositoryProvider.get(), singletonCImpl.provideCategoryRepositoryProvider.get(), singletonCImpl.provideLlmProvider.get());

          case 6: // dev.sandesh.kharcha.feature.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.provideUserPreferencesRepositoryProvider.get(), singletonCImpl.provideTransactionRepositoryProvider.get(), singletonCImpl.provideWorkManagerProvider.get(), viewModelCImpl.dataExporter());

          case 7: // dev.sandesh.kharcha.feature.transactions.TransactionListViewModel 
          return (T) new TransactionListViewModel(singletonCImpl.provideTransactionRepositoryProvider.get(), singletonCImpl.provideCategoryRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends KharchaApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends KharchaApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectKharchaNotificationListenerService(KharchaNotificationListenerService arg0) {
      injectKharchaNotificationListenerService2(arg0);
    }

    private KharchaNotificationListenerService injectKharchaNotificationListenerService2(
        KharchaNotificationListenerService instance) {
      KharchaNotificationListenerService_MembersInjector.injectIngestionService(instance, singletonCImpl.ingestionServiceProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends KharchaApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<KharchaDatabase> provideDatabaseProvider;

    private Provider<BillRepository> provideBillRepositoryProvider;

    private Provider<BillReminderWorker_AssistedFactory> billReminderWorker_AssistedFactoryProvider;

    private Provider<ParserEngine> provideParserEngineProvider;

    private Provider<TransactionRepository> provideTransactionRepositoryProvider;

    private Provider<SmsBulkImportWorker_AssistedFactory> smsBulkImportWorker_AssistedFactoryProvider;

    private Provider<SmsPurgeWorker_AssistedFactory> smsPurgeWorker_AssistedFactoryProvider;

    private Provider<AccountRepository> provideAccountRepositoryProvider;

    private Provider<PromptTemplates> promptTemplatesProvider;

    private Provider<JsonSchemaValidator> jsonSchemaValidatorProvider;

    private Provider<OnDeviceGemmaProvider> onDeviceGemmaProvider;

    private Provider<LlmProvider> provideLlmProvider;

    private Provider<IngestionService> ingestionServiceProvider;

    private Provider<AppLockManager> appLockManagerProvider;

    private Provider<BudgetRepository> provideBudgetRepositoryProvider;

    private Provider<CategoryRepository> provideCategoryRepositoryProvider;

    private Provider<UserPreferencesRepository> provideUserPreferencesRepositoryProvider;

    private Provider<WorkManager> provideWorkManagerProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private BillDao billDao() {
      return DatabaseModule_ProvideBillDaoFactory.provideBillDao(provideDatabaseProvider.get());
    }

    private TransactionDao transactionDao() {
      return DatabaseModule_ProvideTransactionDaoFactory.provideTransactionDao(provideDatabaseProvider.get());
    }

    private TagDao tagDao() {
      return DatabaseModule_ProvideTagDaoFactory.provideTagDao(provideDatabaseProvider.get());
    }

    private Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return MapBuilder.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>newMapBuilder(3).put("dev.sandesh.kharcha.feature.budget.bills.BillReminderWorker", ((Provider) billReminderWorker_AssistedFactoryProvider)).put("dev.sandesh.kharcha.feature.ingestion.SmsBulkImportWorker", ((Provider) smsBulkImportWorker_AssistedFactoryProvider)).put("dev.sandesh.kharcha.feature.ingestion.SmsPurgeWorker", ((Provider) smsPurgeWorker_AssistedFactoryProvider)).build();
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    private AccountDao accountDao() {
      return DatabaseModule_ProvideAccountDaoFactory.provideAccountDao(provideDatabaseProvider.get());
    }

    private BudgetDao budgetDao() {
      return DatabaseModule_ProvideBudgetDaoFactory.provideBudgetDao(provideDatabaseProvider.get());
    }

    private CategoryDao categoryDao() {
      return DatabaseModule_ProvideCategoryDaoFactory.provideCategoryDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<KharchaDatabase>(singletonCImpl, 2));
      this.provideBillRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<BillRepository>(singletonCImpl, 1));
      this.billReminderWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<BillReminderWorker_AssistedFactory>(singletonCImpl, 0));
      this.provideParserEngineProvider = DoubleCheck.provider(new SwitchingProvider<ParserEngine>(singletonCImpl, 4));
      this.provideTransactionRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<TransactionRepository>(singletonCImpl, 5));
      this.smsBulkImportWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<SmsBulkImportWorker_AssistedFactory>(singletonCImpl, 3));
      this.smsPurgeWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<SmsPurgeWorker_AssistedFactory>(singletonCImpl, 6));
      this.provideAccountRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<AccountRepository>(singletonCImpl, 8));
      this.promptTemplatesProvider = DoubleCheck.provider(new SwitchingProvider<PromptTemplates>(singletonCImpl, 11));
      this.jsonSchemaValidatorProvider = DoubleCheck.provider(new SwitchingProvider<JsonSchemaValidator>(singletonCImpl, 12));
      this.onDeviceGemmaProvider = DoubleCheck.provider(new SwitchingProvider<OnDeviceGemmaProvider>(singletonCImpl, 10));
      this.provideLlmProvider = DoubleCheck.provider(new SwitchingProvider<LlmProvider>(singletonCImpl, 9));
      this.ingestionServiceProvider = DoubleCheck.provider(new SwitchingProvider<IngestionService>(singletonCImpl, 7));
      this.appLockManagerProvider = DoubleCheck.provider(new SwitchingProvider<AppLockManager>(singletonCImpl, 13));
      this.provideBudgetRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<BudgetRepository>(singletonCImpl, 14));
      this.provideCategoryRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<CategoryRepository>(singletonCImpl, 15));
      this.provideUserPreferencesRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<UserPreferencesRepository>(singletonCImpl, 16));
      this.provideWorkManagerProvider = DoubleCheck.provider(new SwitchingProvider<WorkManager>(singletonCImpl, 17));
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @Override
    public void injectBootReceiver(BootReceiver arg0) {
    }

    @Override
    public void injectKharchaApplication(KharchaApplication arg0) {
      injectKharchaApplication2(arg0);
    }

    @Override
    public void injectSmsReceiver(SmsReceiver arg0) {
      injectSmsReceiver2(arg0);
    }

    private KharchaApplication injectKharchaApplication2(KharchaApplication instance) {
      KharchaApplication_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      return instance;
    }

    private SmsReceiver injectSmsReceiver2(SmsReceiver instance2) {
      SmsReceiver_MembersInjector.injectIngestionService(instance2, ingestionServiceProvider.get());
      return instance2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dev.sandesh.kharcha.feature.budget.bills.BillReminderWorker_AssistedFactory 
          return (T) new BillReminderWorker_AssistedFactory() {
            @Override
            public BillReminderWorker create(Context appContext, WorkerParameters workerParams) {
              return new BillReminderWorker(appContext, workerParams, singletonCImpl.provideBillRepositoryProvider.get());
            }
          };

          case 1: // dev.sandesh.kharcha.core.model.BillRepository 
          return (T) DatabaseModule_ProvideBillRepositoryFactory.provideBillRepository(singletonCImpl.billDao());

          case 2: // dev.sandesh.kharcha.core.database.KharchaDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // dev.sandesh.kharcha.feature.ingestion.SmsBulkImportWorker_AssistedFactory 
          return (T) new SmsBulkImportWorker_AssistedFactory() {
            @Override
            public SmsBulkImportWorker create(Context appContext2, WorkerParameters workerParams2) {
              return new SmsBulkImportWorker(appContext2, workerParams2, singletonCImpl.provideParserEngineProvider.get(), singletonCImpl.provideTransactionRepositoryProvider.get());
            }
          };

          case 4: // dev.sandesh.kharcha.feature.parser.rules.ParserEngine 
          return (T) AppModule_ProvideParserEngineFactory.provideParserEngine();

          case 5: // dev.sandesh.kharcha.core.model.TransactionRepository 
          return (T) DatabaseModule_ProvideTransactionRepositoryFactory.provideTransactionRepository(singletonCImpl.transactionDao(), singletonCImpl.tagDao());

          case 6: // dev.sandesh.kharcha.feature.ingestion.SmsPurgeWorker_AssistedFactory 
          return (T) new SmsPurgeWorker_AssistedFactory() {
            @Override
            public SmsPurgeWorker create(Context appContext3, WorkerParameters workerParams3) {
              return new SmsPurgeWorker(appContext3, workerParams3, singletonCImpl.provideTransactionRepositoryProvider.get());
            }
          };

          case 7: // dev.sandesh.kharcha.feature.ingestion.service.IngestionService 
          return (T) new IngestionService(singletonCImpl.provideParserEngineProvider.get(), singletonCImpl.provideTransactionRepositoryProvider.get(), singletonCImpl.provideAccountRepositoryProvider.get(), singletonCImpl.provideLlmProvider.get());

          case 8: // dev.sandesh.kharcha.core.model.AccountRepository 
          return (T) DatabaseModule_ProvideAccountRepositoryFactory.provideAccountRepository(singletonCImpl.accountDao());

          case 9: // dev.sandesh.kharcha.feature.llm.LlmProvider 
          return (T) LlmModule_ProvideLlmProviderFactory.provideLlmProvider(singletonCImpl.onDeviceGemmaProvider.get());

          case 10: // dev.sandesh.kharcha.feature.llm.OnDeviceGemmaProvider 
          return (T) new OnDeviceGemmaProvider(singletonCImpl.promptTemplatesProvider.get(), singletonCImpl.jsonSchemaValidatorProvider.get());

          case 11: // dev.sandesh.kharcha.feature.llm.PromptTemplates 
          return (T) new PromptTemplates();

          case 12: // dev.sandesh.kharcha.feature.llm.JsonSchemaValidator 
          return (T) new JsonSchemaValidator();

          case 13: // dev.sandesh.kharcha.security.AppLockManager 
          return (T) new AppLockManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 14: // dev.sandesh.kharcha.core.model.BudgetRepository 
          return (T) DatabaseModule_ProvideBudgetRepositoryFactory.provideBudgetRepository(singletonCImpl.budgetDao());

          case 15: // dev.sandesh.kharcha.core.model.CategoryRepository 
          return (T) DatabaseModule_ProvideCategoryRepositoryFactory.provideCategoryRepository(singletonCImpl.categoryDao());

          case 16: // dev.sandesh.kharcha.core.model.UserPreferencesRepository 
          return (T) AppModule_ProvideUserPreferencesRepositoryFactory.provideUserPreferencesRepository(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 17: // androidx.work.WorkManager 
          return (T) AppModule_ProvideWorkManagerFactory.provideWorkManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
