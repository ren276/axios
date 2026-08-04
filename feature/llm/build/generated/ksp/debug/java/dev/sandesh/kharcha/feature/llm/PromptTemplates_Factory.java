package dev.sandesh.kharcha.feature.llm;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class PromptTemplates_Factory implements Factory<PromptTemplates> {
  @Override
  public PromptTemplates get() {
    return newInstance();
  }

  public static PromptTemplates_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static PromptTemplates newInstance() {
    return new PromptTemplates();
  }

  private static final class InstanceHolder {
    static final PromptTemplates_Factory INSTANCE = new PromptTemplates_Factory();
  }
}
