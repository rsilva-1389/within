package com.within.app.ui.journey;

import androidx.lifecycle.SavedStateHandle;
import com.within.app.billing.BillingManager;
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
public final class JourneyDayViewModel_Factory implements Factory<JourneyDayViewModel> {
  private final Provider<JourneyRepository> journeyRepositoryProvider;

  private final Provider<JourneyProgressRepository> progressRepositoryProvider;

  private final Provider<UserPreferences> userPreferencesProvider;

  private final Provider<BillingManager> billingManagerProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public JourneyDayViewModel_Factory(Provider<JourneyRepository> journeyRepositoryProvider,
      Provider<JourneyProgressRepository> progressRepositoryProvider,
      Provider<UserPreferences> userPreferencesProvider,
      Provider<BillingManager> billingManagerProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.journeyRepositoryProvider = journeyRepositoryProvider;
    this.progressRepositoryProvider = progressRepositoryProvider;
    this.userPreferencesProvider = userPreferencesProvider;
    this.billingManagerProvider = billingManagerProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public JourneyDayViewModel get() {
    return newInstance(journeyRepositoryProvider.get(), progressRepositoryProvider.get(), userPreferencesProvider.get(), billingManagerProvider.get(), savedStateHandleProvider.get());
  }

  public static JourneyDayViewModel_Factory create(
      Provider<JourneyRepository> journeyRepositoryProvider,
      Provider<JourneyProgressRepository> progressRepositoryProvider,
      Provider<UserPreferences> userPreferencesProvider,
      Provider<BillingManager> billingManagerProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new JourneyDayViewModel_Factory(journeyRepositoryProvider, progressRepositoryProvider, userPreferencesProvider, billingManagerProvider, savedStateHandleProvider);
  }

  public static JourneyDayViewModel newInstance(JourneyRepository journeyRepository,
      JourneyProgressRepository progressRepository, UserPreferences userPreferences,
      BillingManager billingManager, SavedStateHandle savedStateHandle) {
    return new JourneyDayViewModel(journeyRepository, progressRepository, userPreferences, billingManager, savedStateHandle);
  }
}
