package dev.sandesh.kharcha.feature.settings;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DataExporter_Factory implements Factory<DataExporter> {
  private final Provider<Context> contextProvider;

  public DataExporter_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public DataExporter get() {
    return newInstance(contextProvider.get());
  }

  public static DataExporter_Factory create(javax.inject.Provider<Context> contextProvider) {
    return new DataExporter_Factory(Providers.asDaggerProvider(contextProvider));
  }

  public static DataExporter_Factory create(Provider<Context> contextProvider) {
    return new DataExporter_Factory(contextProvider);
  }

  public static DataExporter newInstance(Context context) {
    return new DataExporter(context);
  }
}
