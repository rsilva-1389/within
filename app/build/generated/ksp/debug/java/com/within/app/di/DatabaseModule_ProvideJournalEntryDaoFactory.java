package com.within.app.di;

import com.within.app.data.local.WithinDatabase;
import com.within.app.data.local.dao.JournalEntryDao;
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
public final class DatabaseModule_ProvideJournalEntryDaoFactory implements Factory<JournalEntryDao> {
  private final Provider<WithinDatabase> dbProvider;

  public DatabaseModule_ProvideJournalEntryDaoFactory(Provider<WithinDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public JournalEntryDao get() {
    return provideJournalEntryDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideJournalEntryDaoFactory create(
      Provider<WithinDatabase> dbProvider) {
    return new DatabaseModule_ProvideJournalEntryDaoFactory(dbProvider);
  }

  public static JournalEntryDao provideJournalEntryDao(WithinDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideJournalEntryDao(db));
  }
}
