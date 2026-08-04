package dev.sandesh.kharcha.feature.ingestion;

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
    topLevelClass = SmsBulkImportWorker.class
)
public interface SmsBulkImportWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("dev.sandesh.kharcha.feature.ingestion.SmsBulkImportWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(
      SmsBulkImportWorker_AssistedFactory factory);
}
