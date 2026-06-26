package com.within.app.di;

import com.within.app.data.local.WithinDatabase;
import com.within.app.data.local.dao.JourneyProgressDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideJourneyProgressDaoFactory implements Factory<JourneyProgressDao> {
  private final Provider<WithinDatabase> dbProvider;

  public DatabaseModule_ProvideJourneyProgressDaoFactory(Provider<WithinDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public JourneyProgressDao get() {
    return provideJourneyProgressDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideJourneyProgressDaoFactory create(
      Provider<WithinDatabase> dbProvider) {
    return new DatabaseModule_ProvideJourneyProgressDaoFactory(dbProvider);
  }

  public static JourneyProgressDao provideJourneyProgressDao(WithinDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideJourneyProgressDao(db));
  }
}
