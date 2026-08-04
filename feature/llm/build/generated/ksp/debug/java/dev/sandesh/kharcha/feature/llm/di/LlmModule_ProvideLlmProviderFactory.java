package dev.sandesh.kharcha.feature.llm.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.feature.llm.LlmProvider;
import dev.sandesh.kharcha.feature.llm.OnDeviceGemmaProvider;
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
public final class LlmModule_ProvideLlmProviderFactory implements Factory<LlmProvider> {
  private final Provider<OnDeviceGemmaProvider> onDeviceProvider;

  public LlmModule_ProvideLlmProviderFactory(Provider<OnDeviceGemmaProvider> onDeviceProvider) {
    this.onDeviceProvider = onDeviceProvider;
  }

  @Override
  public LlmProvider get() {
    return provideLlmProvider(onDeviceProvider.get());
  }

  public static LlmModule_ProvideLlmProviderFactory create(
      javax.inject.Provider<OnDeviceGemmaProvider> onDeviceProvider) {
    return new LlmModule_ProvideLlmProviderFactory(Providers.asDaggerProvider(onDeviceProvider));
  }

  public static LlmModule_ProvideLlmProviderFactory create(
      Provider<OnDeviceGemmaProvider> onDeviceProvider) {
    return new LlmModule_ProvideLlmProviderFactory(onDeviceProvider);
  }

  public static LlmProvider provideLlmProvider(OnDeviceGemmaProvider onDeviceProvider) {
    return Preconditions.checkNotNullFromProvides(LlmModule.INSTANCE.provideLlmProvider(onDeviceProvider));
  }
}
