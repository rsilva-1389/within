package com.within.app.data.repository

import com.within.app.data.local.dao.DayProgressDao
import com.within.app.data.local.dao.JournalEntryDao
import com.within.app.data.local.dao.JourneyProgressDao
import com.within.app.data.local.entity.DayProgress
import com.within.app.data.local.entity.JournalEntry
import com.within.app.data.local.entity.JourneyProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Progress, completion, and journaling for a journey. Read-only content stays in [JourneyRepository];
 * this owns everything the user *does*. All methods take an explicit journeyId (the active id lives in
 * UserPreferences) so the repository is stateless and testable.
 */
@Singleton
class JourneyProgressRepository @Inject constructor(
    private val progressDao: JourneyProgressDao,
    private val dayProgressDao: DayProgressDao,
    private val journalEntryDao: JournalEntryDao
) {
    private fun today(): Long = LocalDate.now().toEpochDay()

    /** Begin a journey today. Idempotent — keeps the original start date if already begun. */
    suspend fun startJourney(journeyId: String) {
        if (progressDao.progressOnce(journeyId) == null) {
            progressDao.upsert(
                JourneyProgress(journeyId, startedEpochDay = today(), status = STATUS_ACTIVE)
            )
        }
    }

    fun progress(journeyId: String): Flow<JourneyProgress?> = progressDao.progress(journeyId)

    /**
     * Paced unlock: day N becomes available on startedEpochDay + (N-1). Missed days stay open
     * (catch-up); you can't skip ahead. Returns 0 when the journey hasn't started.
     */
    fun availableDay(journeyId: String, lengthDays: Int): Flow<Int> =
        progressDao.progress(journeyId).map { p ->
            if (p == null) 0 else (today() - p.startedEpochDay + 1).toInt().coerceIn(0, lengthDays)
        }

    fun isUnlocked(journeyId: String, day: Int, lengthDays: Int): Flow<Boolean> =
        availableDay(journeyId, lengthDays).map { day in 1..it }

    fun dayProgress(journeyId: String, day: Int): Flow<DayProgress?> =
        dayProgressDao.dayFlow(journeyId, day)

    fun isCompleted(journeyId: String, day: Int): Flow<Boolean> =
        dayProgressDao.dayFlow(journeyId, day).map { it?.completed == true }

    suspend fun markCompleted(journeyId: String, day: Int, completed: Boolean = true) {
        val current = dayProgressDao.dayOnce(journeyId, day) ?: blankDay(journeyId, day)
        dayProgressDao.upsert(
            current.copy(
                completed = completed,
                completedAt = if (completed) System.currentTimeMillis() else null
            )
        )
    }

    suspend fun setMindfulActionDone(journeyId: String, day: Int, done: Boolean) {
        val current = dayProgressDao.dayOnce(journeyId, day) ?: blankDay(journeyId, day)
        dayProgressDao.upsert(current.copy(mindfulActionDone = done))
    }

    /** Autosave per field; blank text removes the entry so empty fields don't linger. */
    suspend fun saveEntry(journeyId: String, day: Int, field: String, text: String) {
        if (text.isBlank()) {
            journalEntryDao.delete(journeyId, day, field)
        } else {
            journalEntryDao.upsert(JournalEntry(journeyId, day, field, text, System.currentTimeMillis()))
        }
    }

    fun entriesFor(journeyId: String, day: Int): Flow<List<JournalEntry>> =
        journalEntryDao.entriesForDay(journeyId, day)

    fun allEntries(journeyId: String): Flow<List<JournalEntry>> =
        journalEntryDao.allEntries(journeyId)

    /** Set of day numbers the user has completed — drives the progress ring and (later) the map. */
    fun completedDays(journeyId: String): Flow<Set<Int>> =
        dayProgressDao.daysFor(journeyId).map { days -> days.filter { it.completed }.map { it.day }.toSet() }

    /** Consecutive completed days ending at the most recently completed day. */
    fun currentStreak(journeyId: String): Flow<Int> =
        dayProgressDao.daysFor(journeyId).map { days ->
            val completed = days.filter { it.completed }.map { it.day }.toSortedSet()
            if (completed.isEmpty()) return@map 0
            var streak = 0
            var d = completed.last()
            while (d in completed) {
                streak++
                d--
            }
            streak
        }

    private fun blankDay(journeyId: String, day: Int) =
        DayProgress(journeyId, day, completed = false, mindfulActionDone = false, completedAt = null)

    companion object {
        const val STATUS_ACTIVE = "active"
        const val STATUS_COMPLETED = "completed"
        const val STATUS_PAUSED = "paused"
    }
}
