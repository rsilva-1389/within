package com.within.app.di;

import android.content.Context;
import com.within.app.data.local.WithinDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DatabaseModule_ProvideWithinDatabaseFactory implements Factory<WithinDatabase> {
  private final Provider<Context> contextProvider;

  public DatabaseModule_ProvideWithinDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public WithinDatabase get() {
    return provideWithinDatabase(contextProvider.get());
  }

  public static DatabaseModule_ProvideWithinDatabaseFactory create(
      Provider<Context> contextProvider) {
    return new DatabaseModule_ProvideWithinDatabaseFactory(contextProvider);
  }

  public static WithinDatabase provideWithinDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideWithinDatabase(context));
  }
}
