package com.within.app.ui.journey;

import com.within.app.data.preferences.UserPreferences;
import com.within.app.data.repository.JourneyProgressRepository;
import com.within.app.data.repository.JourneyRepository;
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
public final class JourneyTodayViewModel_Factory implements Factory<JourneyTodayViewModel> {
  private final Provider<JourneyRepository> journeyRepositoryProvider;

  private final Provider<JourneyProgressRepository> progressRepositoryProvider;

  private final Provider<UserPreferences> userPreferencesProvider;

  public JourneyTodayViewModel_Factory(Provider<JourneyRepository> journeyRepositoryProvider,
      Provider<JourneyProgressRepository> progressRepositoryProvider,
      Provider<UserPreferences> userPreferencesProvider) {
    this.journeyRepositoryProvider = journeyRepositoryProvider;
    this.progressRepositoryProvider = progressRepositoryProvider;
    this.userPreferencesProvider = userPreferencesProvider;
  }

  @Override
  public JourneyTodayViewModel get() {
    return newInstance(journeyRepositoryProvider.get(), progressRepositoryProvider.get(), userPreferencesProvider.get());
  }

  public static JourneyTodayViewModel_Factory create(
      Provider<JourneyRepository> journeyRepositoryProvider,
      Provider<JourneyProgressRepository> progressRepositoryProvider,
      Provider<UserPreferences> userPreferencesProvider) {
    return new JourneyTodayViewModel_Factory(journeyRepositoryProvider, progressRepositoryProvider, userPreferencesProvider);
  }

  public static JourneyTodayViewModel newInstance(JourneyRepository journeyRepository,
      JourneyProgressRepository progressRepository, UserPreferences userPreferences) {
    return new JourneyTodayViewModel(journeyRepository, progressRepository, userPreferences);
  }
}
