package dev.sandesh.kharcha.feature.llm;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class OnDeviceGemmaProvider_Factory implements Factory<OnDeviceGemmaProvider> {
  private final Provider<PromptTemplates> promptTemplatesProvider;

  private final Provider<JsonSchemaValidator> jsonValidatorProvider;

  public OnDeviceGemmaProvider_Factory(Provider<PromptTemplates> promptTemplatesProvider,
      Provider<JsonSchemaValidator> jsonValidatorProvider) {
    this.promptTemplatesProvider = promptTemplatesProvider;
    this.jsonValidatorProvider = jsonValidatorProvider;
  }

  @Override
  public OnDeviceGemmaProvider get() {
    return newInstance(promptTemplatesProvider.get(), jsonValidatorProvider.get());
  }

  public static OnDeviceGemmaProvider_Factory create(
      javax.inject.Provider<PromptTemplates> promptTemplatesProvider,
      javax.inject.Provider<JsonSchemaValidator> jsonValidatorProvider) {
    return new OnDeviceGemmaProvider_Factory(Providers.asDaggerProvider(promptTemplatesProvider), Providers.asDaggerProvider(jsonValidatorProvider));
  }

  public static OnDeviceGemmaProvider_Factory create(
      Provider<PromptTemplates> promptTemplatesProvider,
      Provider<JsonSchemaValidator> jsonValidatorProvider) {
    return new OnDeviceGemmaProvider_Factory(promptTemplatesProvider, jsonValidatorProvider);
  }

  public static OnDeviceGemmaProvider newInstance(PromptTemplates promptTemplates,
      JsonSchemaValidator jsonValidator) {
    return new OnDeviceGemmaProvider(promptTemplates, jsonValidator);
  }
}
