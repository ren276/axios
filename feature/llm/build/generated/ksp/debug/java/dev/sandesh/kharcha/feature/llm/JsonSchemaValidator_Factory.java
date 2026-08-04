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
public final class JsonSchemaValidator_Factory implements Factory<JsonSchemaValidator> {
  @Override
  public JsonSchemaValidator get() {
    return newInstance();
  }

  public static JsonSchemaValidator_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static JsonSchemaValidator newInstance() {
    return new JsonSchemaValidator();
  }

  private static final class InstanceHolder {
    static final JsonSchemaValidator_Factory INSTANCE = new JsonSchemaValidator_Factory();
  }
}
