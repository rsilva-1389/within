package com.within.app.data.local.entity

import androidx.room.Entity

/** Free text the user writes for a given day + section. The retention moat. */
@Entity(tableName = "journal_entries", primaryKeys = ["journeyId", "day", "field"])
data class JournalEntry(
    val journeyId: String,
    val day: Int,
    val field: String, // "prompt" | "deepDive0".."deepDive2" | "evening" | "note"
    val text: String,
    val updatedAt: Long
)
