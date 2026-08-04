package dev.sandesh.kharcha.app.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.sandesh.kharcha.feature.parser.rules.ParserEngine;
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
public final class AppModule_ProvideParserEngineFactory implements Factory<ParserEngine> {
  @Override
  public ParserEngine get() {
    return provideParserEngine();
  }

  public static AppModule_ProvideParserEngineFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ParserEngine provideParserEngine() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideParserEngine());
  }

  private static final class InstanceHolder {
    static final AppModule_ProvideParserEngineFactory INSTANCE = new AppModule_ProvideParserEngineFactory();
  }
}
