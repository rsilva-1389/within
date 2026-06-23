package com.within.app.ui.onboarding;

import android.content.Context;
import com.within.app.data.preferences.UserPreferences;
import com.within.app.notification.NotificationScheduler;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
    "cast"
})
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<UserPreferences> userPreferencesProvider;

  private final Provider<NotificationScheduler> notificationSchedulerProvider;

  public OnboardingViewModel_Factory(Provider<Context> contextProvider,
      Provider<UserPreferences> userPreferencesProvider,
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    this.contextProvider = contextProvider;
    this.userPreferencesProvider = userPreferencesProvider;
    this.notificationSchedulerProvider = notificationSchedulerProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(contextProvider.get(), userPreferencesProvider.get(), notificationSchedulerProvider.get());
  }

  public static OnboardingViewModel_Factory create(Provider<Context> contextProvider,
      Provider<UserPreferences> userPreferencesProvider,
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    return new OnboardingViewModel_Factory(contextProvider, userPreferencesProvider, notificationSchedulerProvider);
  }

  public static OnboardingViewModel newInstance(Context context, UserPreferences userPreferences,
      NotificationScheduler notificationScheduler) {
    return new OnboardingViewModel(context, userPreferences, notificationScheduler);
  }
}
