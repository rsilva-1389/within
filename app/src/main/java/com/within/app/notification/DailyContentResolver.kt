package com.within.app.notification

import com.within.app.data.preferences.UserPreferences
import com.within.app.data.repository.JourneyProgressRepository
import com.within.app.data.repository.JourneyRepository
import com.within.app.data.repository.MessageRepository
import kotlinx.coroutines.flow.first
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/** What a daily notification should say, resolved at fire time. */
sealed interface DailyContent {
    data class Journey(val journeyId: String, val day: Int, val text: String) : DailyContent
    data class Message(val messageId: String, val text: String) : DailyContent
    data object None : DailyContent
}

/**
 * Chooses the daily notification payload: an active, unlocked, *incomplete* journey day
 * (morning → prompt, evening → evening reflection) — otherwise the message stream as a
 * graceful fallback. This is the single unification point the notification infra reuses.
 */
@Singleton
class DailyContentResolver @Inject constructor(
    private val journeyRepository: JourneyRepository,
    private val journeyProgressRepository: JourneyProgressRepository,
    private val messageRepository: MessageRepository,
    private val userPreferences: UserPreferences
) {
    suspend fun resolve(now: LocalTime = LocalTime.now()): DailyContent {
        val activeJourneyId = userPreferences.activeJourneyId.first()
        val journey = activeJourneyId?.let { journeyRepository.getJourney(it) }
        if (journey != null) {
            val availableDay = journeyProgressRepository
                .availableDay(journey.id, journey.lengthDays).first()
            val day = journeyRepository.getDay(journey.id, availableDay)
            val incomplete = day != null &&
                !journeyProgressRepository.isCompleted(journey.id, availableDay).first()
            if (day != null && incomplete) {
                val text = if (now.hour >= EVENING_HOUR) day.eveningReflection else day.prompt
                return DailyContent.Journey(journey.id, availableDay, text)
            }
        }
        // Fallback: the existing message stream (free / between-journeys / completed-today).
        val categories = userPreferences.enabledCategories.first()
        val shownIds = userPreferences.shownMessageIds.first()
        val message = messageRepository.getRandomMessage(categories, shownIds)
            ?: return DailyContent.None
        return DailyContent.Message(message.id, message.text)
    }

    companion object {
        /** At/after this hour, the journey notification carries the evening reflection. */
        const val EVENING_HOUR = 17
    }
}
