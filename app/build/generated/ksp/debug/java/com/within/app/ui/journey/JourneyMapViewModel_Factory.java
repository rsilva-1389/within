package com.within.app.ui.journey;

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
public final class JourneyMapViewModel_Factory implements Factory<JourneyMapViewModel> {
  private final Provider<JourneyRepository> journeyRepositoryProvider;

  private final Provider<JourneyProgressRepository> progressRepositoryProvider;

  public JourneyMapViewModel_Factory(Provider<JourneyRepository> journeyRepositoryProvider,
      Provider<JourneyProgressRepository> progressRepositoryProvider) {
    this.journeyRepositoryProvider = journeyRepositoryProvider;
    this.progressRepositoryProvider = progressRepositoryProvider;
  }

  @Override
  public JourneyMapViewModel get() {
    return newInstance(journeyRepositoryProvider.get(), progressRepositoryProvider.get());
  }

  public static JourneyMapViewModel_Factory create(
      Provider<JourneyRepository> journeyRepositoryProvider,
      Provider<JourneyProgressRepository> progressRepositoryProvider) {
    return new JourneyMapViewModel_Factory(journeyRepositoryProvider, progressRepositoryProvider);
  }

  public static JourneyMapViewModel newInstance(JourneyRepository journeyRepository,
      JourneyProgressRepository progressRepository) {
    return new JourneyMapViewModel(journeyRepository, progressRepository);
  }
}
