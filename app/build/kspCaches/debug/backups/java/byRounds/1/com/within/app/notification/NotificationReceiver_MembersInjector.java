package com.within.app.notification;

import com.within.app.data.preferences.UserPreferences;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class NotificationReceiver_MembersInjector implements MembersInjector<NotificationReceiver> {
  private final Provider<DailyContentResolver> dailyContentResolverProvider;

  private final Provider<UserPreferences> userPreferencesProvider;

  private final Provider<NotificationScheduler> notificationSchedulerProvider;

  public NotificationReceiver_MembersInjector(
      Provider<DailyContentResolver> dailyContentResolverProvider,
      Provider<UserPreferences> userPreferencesProvider,
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    this.dailyContentResolverProvider = dailyContentResolverProvider;
    this.userPreferencesProvider = userPreferencesProvider;
    this.notificationSchedulerProvider = notificationSchedulerProvider;
  }

  public static MembersInjector<NotificationReceiver> create(
      Provider<DailyContentResolver> dailyContentResolverProvider,
      Provider<UserPreferences> userPreferencesProvider,
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    return new NotificationReceiver_MembersInjector(dailyContentResolverProvider, userPreferencesProvider, notificationSchedulerProvider);
  }

  @Override
  public void injectMembers(NotificationReceiver instance) {
    injectDailyContentResolver(instance, dailyContentResolverProvider.get());
    injectUserPreferences(instance, userPreferencesProvider.get());
    injectNotificationScheduler(instance, notificationSchedulerProvider.get());
  }

  @InjectedFieldSignature("com.within.app.notification.NotificationReceiver.dailyContentResolver")
  public static void injectDailyContentResolver(NotificationReceiver instance,
      DailyContentResolver dailyContentResolver) {
    instance.dailyContentResolver = dailyContentResolver;
  }

  @InjectedFieldSignature("com.within.app.notification.NotificationReceiver.userPreferences")
  public static void injectUserPreferences(NotificationReceiver instance,
      UserPreferences userPreferences) {
    instance.userPreferences = userPreferences;
  }

  @InjectedFieldSignature("com.within.app.notification.NotificationReceiver.notificationScheduler")
  public static void injectNotificationScheduler(NotificationReceiver instance,
      NotificationScheduler notificationScheduler) {
    instance.notificationScheduler = notificationScheduler;
  }
}
