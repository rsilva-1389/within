package com.within.app.ui.home;

import com.within.app.data.preferences.UserPreferences;
import com.within.app.data.repository.MessageRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<MessageRepository> messageRepositoryProvider;

  private final Provider<UserPreferences> userPreferencesProvider;

  public HomeViewModel_Factory(Provider<MessageRepository> messageRepositoryProvider,
      Provider<UserPreferences> userPreferencesProvider) {
    this.messageRepositoryProvider = messageRepositoryProvider;
    this.userPreferencesProvider = userPreferencesProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(messageRepositoryProvider.get(), userPreferencesProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<MessageRepository> messageRepositoryProvider,
      Provider<UserPreferences> userPreferencesProvider) {
    return new HomeViewModel_Factory(messageRepositoryProvider, userPreferencesProvider);
  }

  public static HomeViewModel newInstance(MessageRepository messageRepository,
      UserPreferences userPreferences) {
    return new HomeViewModel(messageRepository, userPreferences);
  }
}
