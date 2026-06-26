package com.within.app.billing;

import android.content.Context;
import com.within.app.data.preferences.UserPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
    "cast"
})
public final class BillingManager_Factory implements Factory<BillingManager> {
  private final Provider<Context> contextProvider;

  private final Provider<UserPreferences> userPreferencesProvider;

  public BillingManager_Factory(Provider<Context> contextProvider,
      Provider<UserPreferences> userPreferencesProvider) {
    this.contextProvider = contextProvider;
    this.userPreferencesProvider = userPreferencesProvider;
  }

  @Override
  public BillingManager get() {
    return newInstance(contextProvider.get(), userPreferencesProvider.get());
  }

  public static BillingManager_Factory create(Provider<Context> contextProvider,
      Provider<UserPreferences> userPreferencesProvider) {
    return new BillingManager_Factory(contextProvider, userPreferencesProvider);
  }

  public static BillingManager newInstance(Context context, UserPreferences userPreferences) {
    return new BillingManager(context, userPreferences);
  }
}
