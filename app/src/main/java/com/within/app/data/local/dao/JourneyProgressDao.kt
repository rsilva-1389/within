package com.within.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.within.app.data.local.entity.JourneyProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface JourneyProgressDao {

    @Upsert
    suspend fun upsert(progress: JourneyProgress)

    @Query("SELECT * FROM journey_progress WHERE journeyId = :journeyId")
    fun progress(journeyId: String): Flow<JourneyProgress?>

    @Query("SELECT * FROM journey_progress WHERE journeyId = :journeyId")
    suspend fun progressOnce(journeyId: String): JourneyProgress?
}
