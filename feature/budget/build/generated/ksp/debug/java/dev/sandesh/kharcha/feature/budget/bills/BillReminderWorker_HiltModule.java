package dev.sandesh.kharcha.feature.budget.bills;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = BillReminderWorker.class
)
public interface BillReminderWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("dev.sandesh.kharcha.feature.budget.bills.BillReminderWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(
      BillReminderWorker_AssistedFactory factory);
}
