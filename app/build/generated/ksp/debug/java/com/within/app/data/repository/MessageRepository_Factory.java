package com.within.app.data.repository;

import android.content.Context;
import com.google.gson.Gson;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class MessageRepository_Factory implements Factory<MessageRepository> {
  private final Provider<Context> contextProvider;

  private final Provider<Gson> gsonProvider;

  public MessageRepository_Factory(Provider<Context> contextProvider, Provider<Gson> gsonProvider) {
    this.contextProvider = contextProvider;
    this.gsonProvider = gsonProvider;
  }

  @Override
  public MessageRepository get() {
    return newInstance(contextProvider.get(), gsonProvider.get());
  }

  public static MessageRepository_Factory create(Provider<Context> contextProvider,
      Provider<Gson> gsonProvider) {
    return new MessageRepository_Factory(contextProvider, gsonProvider);
  }

  public static MessageRepository newInstance(Context context, Gson gson) {
    return new MessageRepository(context, gson);
  }
}
