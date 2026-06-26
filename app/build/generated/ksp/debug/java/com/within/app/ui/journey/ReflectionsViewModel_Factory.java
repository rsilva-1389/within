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
public final class ReflectionsViewModel_Factory implements Factory<ReflectionsViewModel> {
  private final Provider<JourneyRepository> journeyRepositoryProvider;

  private final Provider<JourneyProgressRepository> progressRepositoryProvider;

  public ReflectionsViewModel_Factory(Provider<JourneyRepository> journeyRepositoryProvider,
      Provider<JourneyProgressRepository> progressRepositoryProvider) {
    this.journeyRepositoryProvider = journeyRepositoryProvider;
    this.progressRepositoryProvider = progressRepositoryProvider;
  }

  @Override
  public ReflectionsViewModel get() {
    return newInstance(journeyRepositoryProvider.get(), progressRepositoryProvider.get());
  }

  public static ReflectionsViewModel_Factory create(
      Provider<JourneyRepository> journeyRepositoryProvider,
      Provider<JourneyProgressRepository> progressRepositoryProvider) {
    return new ReflectionsViewModel_Factory(journeyRepositoryProvider, progressRepositoryProvider);
  }

  public static ReflectionsViewModel newInstance(JourneyRepository journeyRepository,
      JourneyProgressRepository progressRepository) {
    return new ReflectionsViewModel(journeyRepository, progressRepository);
  }
}
