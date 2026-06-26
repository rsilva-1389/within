package com.within.app.ui.journey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.within.app.data.preferences.UserPreferences
import com.within.app.data.repository.JourneyProgressRepository
import com.within.app.data.repository.JourneyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JourneyTodayUiState(
    val isLoading: Boolean = true,
    val started: Boolean = false,
    val journeyId: String = "",
    val title: String = "",
    val subtitle: String = "",
    val tagline: String = "",
    val totalDays: Int = 30,
    val availableDay: Int = 0,
    val completedCount: Int = 0,
    val streak: Int = 0,
    val focusDay: Int = 1,
    val focusTitle: String = "",
    val focusTheme: String = "",
    val phaseTitle: String = "",
    val focusCompleted: Boolean = false,
    val isPremium: Boolean = false
)

@HiltViewModel
class JourneyTodayViewModel @Inject constructor(
    private val journeyRepository: JourneyRepository,
    private val progressRepository: JourneyProgressRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val journey = journeyRepository.getJourney()

    val state: StateFlow<JourneyTodayUiState> = combine(
        userPreferences.activeJourneyId,
        progressRepository.availableDay(journey.id, journey.lengthDays),
        progressRepository.currentStreak(journey.id),
        progressRepository.completedDays(journey.id),
        userPreferences.isPremium
    ) { activeId, availableDay, streak, completed, premium ->
        val started = activeId == journey.id && availableDay > 0
        val focusDay = availableDay.coerceIn(1, journey.lengthDays)
        JourneyTodayUiState(
            isLoading = false,
            started = started,
            journeyId = journey.id,
            title = journey.title,
            subtitle = journey.subtitle,
            tagline = journey.tagline,
            totalDays = journey.lengthDays,
            availableDay = availableDay,
            completedCount = completed.size,
            streak = streak,
            focusDay = focusDay,
            focusTitle = journeyRepository.getDay(journey.id, focusDay)?.title.orEmpty(),
            focusTheme = journeyRepository.getDay(journey.id, focusDay)?.theme.orEmpty(),
            phaseTitle = journeyRepository.phaseForDay(journey.id, focusDay)?.title.orEmpty(),
            focusCompleted = focusDay in completed,
            isPremium = premium
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), JourneyTodayUiState())

    fun startJourney() {
        viewModelScope.launch {
            progressRepository.startJourney(journey.id)
            userPreferences.setActiveJourneyId(journey.id)
        }
    }
}
