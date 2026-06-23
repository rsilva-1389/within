package com.within.app.notification;

import com.within.app.data.preferences.UserPreferences;
import com.within.app.data.repository.MessageRepository;
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
  private final Provider<MessageRepository> messageRepositoryProvider;

  private final Provider<UserPreferences> userPreferencesProvider;

  private final Provider<NotificationScheduler> notificationSchedulerProvider;

  public NotificationReceiver_MembersInjector(Provider<MessageRepository> messageRepositoryProvider,
      Provider<UserPreferences> userPreferencesProvider,
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    this.messageRepositoryProvider = messageRepositoryProvider;
    this.userPreferencesProvider = userPreferencesProvider;
    this.notificationSchedulerProvider = notificationSchedulerProvider;
  }

  public static MembersInjector<NotificationReceiver> create(
      Provider<MessageRepository> messageRepositoryProvider,
      Provider<UserPreferences> userPreferencesProvider,
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    return new NotificationReceiver_MembersInjector(messageRepositoryProvider, userPreferencesProvider, notificationSchedulerProvider);
  }

  @Override
  public void injectMembers(NotificationReceiver instance) {
    injectMessageRepository(instance, messageRepositoryProvider.get());
    injectUserPreferences(instance, userPreferencesProvider.get());
    injectNotificationScheduler(instance, notificationSchedulerProvider.get());
  }

  @InjectedFieldSignature("com.within.app.notification.NotificationReceiver.messageRepository")
  public static void injectMessageRepository(NotificationReceiver instance,
      MessageRepository messageRepository) {
    instance.messageRepository = messageRepository;
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
