package com.within.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.within.app.data.local.entity.DayProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface DayProgressDao {

    @Upsert
    suspend fun upsert(dayProgress: DayProgress)

    @Query("SELECT * FROM day_progress WHERE journeyId = :journeyId AND day = :day")
    fun dayFlow(journeyId: String, day: Int): Flow<DayProgress?>

    @Query("SELECT * FROM day_progress WHERE journeyId = :journeyId AND day = :day")
    suspend fun dayOnce(journeyId: String, day: Int): DayProgress?

    @Query("SELECT * FROM day_progress WHERE journeyId = :journeyId ORDER BY day")
    fun daysFor(journeyId: String): Flow<List<DayProgress>>
}
