package com.within.app.data.repository;

import com.within.app.data.local.dao.DayProgressDao;
import com.within.app.data.local.dao.JournalEntryDao;
import com.within.app.data.local.dao.JourneyProgressDao;
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
public final class JourneyProgressRepository_Factory implements Factory<JourneyProgressRepository> {
  private final Provider<JourneyProgressDao> progressDaoProvider;

  private final Provider<DayProgressDao> dayProgressDaoProvider;

  private final Provider<JournalEntryDao> journalEntryDaoProvider;

  public JourneyProgressRepository_Factory(Provider<JourneyProgressDao> progressDaoProvider,
      Provider<DayProgressDao> dayProgressDaoProvider,
      Provider<JournalEntryDao> journalEntryDaoProvider) {
    this.progressDaoProvider = progressDaoProvider;
    this.dayProgressDaoProvider = dayProgressDaoProvider;
    this.journalEntryDaoProvider = journalEntryDaoProvider;
  }

  @Override
  public JourneyProgressRepository get() {
    return newInstance(progressDaoProvider.get(), dayProgressDaoProvider.get(), journalEntryDaoProvider.get());
  }

  public static JourneyProgressRepository_Factory create(
      Provider<JourneyProgressDao> progressDaoProvider,
      Provider<DayProgressDao> dayProgressDaoProvider,
      Provider<JournalEntryDao> journalEntryDaoProvider) {
    return new JourneyProgressRepository_Factory(progressDaoProvider, dayProgressDaoProvider, journalEntryDaoProvider);
  }

  public static JourneyProgressRepository newInstance(JourneyProgressDao progressDao,
      DayProgressDao dayProgressDao, JournalEntryDao journalEntryDao) {
    return new JourneyProgressRepository(progressDao, dayProgressDao, journalEntryDao);
  }
}
