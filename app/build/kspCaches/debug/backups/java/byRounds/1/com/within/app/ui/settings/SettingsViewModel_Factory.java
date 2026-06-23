package com.within.app.ui.settings;

import com.within.app.data.preferences.UserPreferences;
import com.within.app.notification.NotificationScheduler;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<UserPreferences> userPreferencesProvider;

  private final Provider<NotificationScheduler> notificationSchedulerProvider;

  public SettingsViewModel_Factory(Provider<UserPreferences> userPreferencesProvider,
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    this.userPreferencesProvider = userPreferencesProvider;
    this.notificationSchedulerProvider = notificationSchedulerProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(userPreferencesProvider.get(), notificationSchedulerProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<UserPreferences> userPreferencesProvider,
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    return new SettingsViewModel_Factory(userPreferencesProvider, notificationSchedulerProvider);
  }

  public static SettingsViewModel newInstance(UserPreferences userPreferences,
      NotificationScheduler notificationScheduler) {
    return new SettingsViewModel(userPreferences, notificationScheduler);
  }
}
