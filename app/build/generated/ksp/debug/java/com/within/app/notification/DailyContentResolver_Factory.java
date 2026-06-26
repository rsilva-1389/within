package com.within.app.notification;

import com.within.app.data.preferences.UserPreferences;
import com.within.app.data.repository.JourneyProgressRepository;
import com.within.app.data.repository.JourneyRepository;
import com.within.app.data.repository.MessageRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
    "cast"
})
public final class DailyContentResolver_Factory implements Factory<DailyContentResolver> {
  private final Provider<JourneyRepository> journeyRepositoryProvider;

  private final Provider<JourneyProgressRepository> journeyProgressRepositoryProvider;

  private final Provider<MessageRepository> messageRepositoryProvider;

  private final Provider<UserPreferences> userPreferencesProvider;

  public DailyContentResolver_Factory(Provider<JourneyRepository> journeyRepositoryProvider,
      Provider<JourneyProgressRepository> journeyProgressRepositoryProvider,
      Provider<MessageRepository> messageRepositoryProvider,
      Provider<UserPreferences> userPreferencesProvider) {
    this.journeyRepositoryProvider = journeyRepositoryProvider;
    this.journeyProgressRepositoryProvider = journeyProgressRepositoryProvider;
    this.messageRepositoryProvider = messageRepositoryProvider;
    this.userPreferencesProvider = userPreferencesProvider;
  }

  @Override
  public DailyContentResolver get() {
    return newInstance(journeyRepositoryProvider.get(), journeyProgressRepositoryProvider.get(), messageRepositoryProvider.get(), userPreferencesProvider.get());
  }

  public static DailyContentResolver_Factory create(
      Provider<JourneyRepository> journeyRepositoryProvider,
      Provider<JourneyProgressRepository> journeyProgressRepositoryProvider,
      Provider<MessageRepository> messageRepositoryProvider,
      Provider<UserPreferences> userPreferencesProvider) {
    return new DailyContentResolver_Factory(journeyRepositoryProvider, journeyProgressRepositoryProvider, messageRepositoryProvider, userPreferencesProvider);
  }

  public static DailyContentResolver newInstance(JourneyRepository journeyRepository,
      JourneyProgressRepository journeyProgressRepository, MessageRepository messageRepository,
      UserPreferences userPreferences) {
    return new DailyContentResolver(journeyRepository, journeyProgressRepository, messageRepository, userPreferences);
  }
}
