package com.within.app.di

import android.content.Context
import androidx.room.Room
import com.within.app.data.local.WithinDatabase
import com.within.app.data.local.dao.DayProgressDao
import com.within.app.data.local.dao.JournalEntryDao
import com.within.app.data.local.dao.JourneyProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWithinDatabase(@ApplicationContext context: Context): WithinDatabase =
        Room.databaseBuilder(context, WithinDatabase::class.java, "within.db").build()

    @Provides
    fun provideJournalEntryDao(db: WithinDatabase): JournalEntryDao = db.journalEntryDao()

    @Provides
    fun provideJourneyProgressDao(db: WithinDatabase): JourneyProgressDao = db.journeyProgressDao()

    @Provides
    fun provideDayProgressDao(db: WithinDatabase): DayProgressDao = db.dayProgressDao()
}
