package com.within.app.data.local.entity

import androidx.room.Entity

/**
 * Per-day completion. Separate from [JournalEntry] because journaling is optional —
 * a day can be completed with nothing written — and the mindful action is independently checkable.
 */
@Entity(tableName = "day_progress", primaryKeys = ["journeyId", "day"])
data class DayProgress(
    val journeyId: String,
    val day: Int,
    val completed: Boolean,
    val mindfulActionDone: Boolean,
    val completedAt: Long?
)
