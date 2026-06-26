package com.within.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.within.app.data.local.entity.JournalEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalEntryDao {

    @Upsert
    suspend fun upsert(entry: JournalEntry)

    @Query("DELETE FROM journal_entries WHERE journeyId = :journeyId AND day = :day AND field = :field")
    suspend fun delete(journeyId: String, day: Int, field: String)

    @Query("SELECT * FROM journal_entries WHERE journeyId = :journeyId AND day = :day")
    fun entriesForDay(journeyId: String, day: Int): Flow<List<JournalEntry>>

    @Query("SELECT * FROM journal_entries WHERE journeyId = :journeyId ORDER BY day, field")
    fun allEntries(journeyId: String): Flow<List<JournalEntry>>
}
