package com.within.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.within.app.data.local.dao.DayProgressDao
import com.within.app.data.local.dao.JournalEntryDao
import com.within.app.data.local.dao.JourneyProgressDao
import com.within.app.data.local.entity.DayProgress
import com.within.app.data.local.entity.JournalEntry
import com.within.app.data.local.entity.JourneyProgress

@Database(
    entities = [JournalEntry::class, JourneyProgress::class, DayProgress::class],
    version = 1,
    exportSchema = false
)
abstract class WithinDatabase : RoomDatabase() {
    abstract fun journalEntryDao(): JournalEntryDao
    abstract fun journeyProgressDao(): JourneyProgressDao
    abstract fun dayProgressDao(): DayProgressDao
}
