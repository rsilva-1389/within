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
public final class BootReceiver_MembersInjector implements MembersInjector<BootReceiver> {
  private final Provider<UserPreferences> userPreferencesProvider;

  private final Provider<NotificationScheduler> notificationSchedulerProvider;

  public BootReceiver_MembersInjector(Provider<UserPreferences> userPreferencesProvider,
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    this.userPreferencesProvider = userPreferencesProvider;
    this.notificationSchedulerProvider = notificationSchedulerProvider;
  }

  public static MembersInjector<BootReceiver> create(
      Provider<UserPreferences> userPreferencesProvider,
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    return new BootReceiver_MembersInjector(userPreferencesProvider, notificationSchedulerProvider);
  }

  @Override
  public void injectMembers(BootReceiver instance) {
    injectUserPreferences(instance, userPreferencesProvider.get());
    injectNotificationScheduler(instance, notificationSchedulerProvider.get());
  }

  @InjectedFieldSignature("com.within.app.notification.BootReceiver.userPreferences")
  public static void injectUserPreferences(BootReceiver instance, UserPreferences userPreferences) {
    instance.userPreferences = userPreferences;
  }

  @InjectedFieldSignature("com.within.app.notification.BootReceiver.notificationScheduler")
  public static void injectNotificationScheduler(BootReceiver instance,
      NotificationScheduler notificationScheduler) {
    instance.notificationScheduler = notificationScheduler;
  }
}
