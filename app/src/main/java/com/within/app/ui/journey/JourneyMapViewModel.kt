package com.within.app.ui.journey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.within.app.data.repository.JourneyProgressRepository
import com.within.app.data.repository.JourneyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class DayStatus { Completed, Available, Locked }

data class MapDay(
    val day: Int,
    val title: String,
    val theme: String,
    val status: DayStatus
)

data class MapPhase(
    val id: String,
    val title: String,
    val days: List<MapDay>
)

data class JourneyMapUiState(
    val isLoading: Boolean = true,
    val journeyId: String = "",
    val totalDays: Int = 30,
    val completedCount: Int = 0,
    val phases: List<MapPhase> = emptyList()
)

@HiltViewModel
class JourneyMapViewModel @Inject constructor(
    private val journeyRepository: JourneyRepository,
    progressRepository: JourneyProgressRepository
) : ViewModel() {

    private val journey = journeyRepository.getJourney()

    val state: StateFlow<JourneyMapUiState> = combine(
        progressRepository.availableDay(journey.id, journey.lengthDays),
        progressRepository.completedDays(journey.id)
    ) { availableDay, completed ->
        val phases = journey.phases.map { phase ->
            val days = journey.days
                .filter { it.day in phase.from..phase.to }
                .map { d ->
                    val status = when {
                        d.day in completed -> DayStatus.Completed
                        d.day <= availableDay -> DayStatus.Available
                        else -> DayStatus.Locked
                    }
                    MapDay(d.day, d.title, d.theme, status)
                }
            MapPhase(phase.id, phase.title, days)
        }
        JourneyMapUiState(
            isLoading = false,
            journeyId = journey.id,
            totalDays = journey.lengthDays,
            completedCount = completed.size,
            phases = phases
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), JourneyMapUiState())
}
