package dev.sandesh.kharcha.feature.budget.bills;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.core.model.BillRepository;
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
public final class BillReminderWorker_Factory {
  private final Provider<BillRepository> billRepositoryProvider;

  public BillReminderWorker_Factory(Provider<BillRepository> billRepositoryProvider) {
    this.billRepositoryProvider = billRepositoryProvider;
  }

  public BillReminderWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams, billRepositoryProvider.get());
  }

  public static BillReminderWorker_Factory create(
      javax.inject.Provider<BillRepository> billRepositoryProvider) {
    return new BillReminderWorker_Factory(Providers.asDaggerProvider(billRepositoryProvider));
  }

  public static BillReminderWorker_Factory create(Provider<BillRepository> billRepositoryProvider) {
    return new BillReminderWorker_Factory(billRepositoryProvider);
  }

  public static BillReminderWorker newInstance(Context appContext, WorkerParameters workerParams,
      BillRepository billRepository) {
    return new BillReminderWorker(appContext, workerParams, billRepository);
  }
}
