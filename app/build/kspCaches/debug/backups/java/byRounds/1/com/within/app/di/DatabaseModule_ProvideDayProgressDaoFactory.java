package com.within.app.di;

import com.within.app.data.local.WithinDatabase;
import com.within.app.data.local.dao.DayProgressDao;
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
public final class DatabaseModule_ProvideDayProgressDaoFactory implements Factory<DayProgressDao> {
  private final Provider<WithinDatabase> dbProvider;

  public DatabaseModule_ProvideDayProgressDaoFactory(Provider<WithinDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public DayProgressDao get() {
    return provideDayProgressDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideDayProgressDaoFactory create(
      Provider<WithinDatabase> dbProvider) {
    return new DatabaseModule_ProvideDayProgressDaoFactory(dbProvider);
  }

  public static DayProgressDao provideDayProgressDao(WithinDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideDayProgressDao(db));
  }
}
