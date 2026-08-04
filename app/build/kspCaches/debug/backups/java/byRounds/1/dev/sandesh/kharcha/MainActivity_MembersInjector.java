package dev.sandesh.kharcha;

import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dev.sandesh.kharcha.security.AppLockManager;
import javax.annotation.processing.Generated;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<AppLockManager> appLockManagerProvider;

  public MainActivity_MembersInjector(Provider<AppLockManager> appLockManagerProvider) {
    this.appLockManagerProvider = appLockManagerProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<AppLockManager> appLockManagerProvider) {
    return new MainActivity_MembersInjector(appLockManagerProvider);
  }

  public static MembersInjector<MainActivity> create(
      javax.inject.Provider<AppLockManager> appLockManagerProvider) {
    return new MainActivity_MembersInjector(Providers.asDaggerProvider(appLockManagerProvider));
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectAppLockManager(instance, appLockManagerProvider.get());
  }

  @InjectedFieldSignature("dev.sandesh.kharcha.MainActivity.appLockManager")
  public static void injectAppLockManager(MainActivity instance, AppLockManager appLockManager) {
    instance.appLockManager = appLockManager;
  }
}
