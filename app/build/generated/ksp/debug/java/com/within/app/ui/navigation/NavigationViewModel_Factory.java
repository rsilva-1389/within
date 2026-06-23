package com.within.app.ui.navigation;

import com.within.app.data.preferences.UserPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
    "cast"
})
public final class NavigationViewModel_Factory implements Factory<NavigationViewModel> {
  private final Provider<UserPreferences> preferencesProvider;

  public NavigationViewModel_Factory(Provider<UserPreferences> preferencesProvider) {
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public NavigationViewModel get() {
    return newInstance(preferencesProvider.get());
  }

  public static NavigationViewModel_Factory create(Provider<UserPreferences> preferencesProvider) {
    return new NavigationViewModel_Factory(preferencesProvider);
  }

  public static NavigationViewModel newInstance(UserPreferences preferences) {
    return new NavigationViewModel(preferences);
  }
}
