package com.within.app.data.local.entity

import androidx.room.Entity

/**
 * Journey-level state. `currentDay`/`lastCompletedDay` are intentionally *not* stored —
 * they're derived (available day from [startedEpochDay] + today; last completed from [DayProgress])
 * to avoid persisted-state drift.
 */
@Entity(tableName = "journey_progress", primaryKeys = ["journeyId"])
data class JourneyProgress(
    val journeyId: String,
    val startedEpochDay: Long,
    val status: String // "active" | "completed" | "paused"
)
