package com.within.app.ui.journey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.within.app.data.repository.JourneyProgressRepository
import com.within.app.data.repository.JourneyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** The kind of journaling field, so the UI can label it without parsing raw field keys. */
enum class ReflectionFieldKind { Prompt, DeepDive, Evening, Note }

data class ReflectionEntry(
    val field: String,
    val kind: ReflectionFieldKind,
    val text: String
)

data class ReflectionDay(
    val day: Int,
    val title: String,
    val entries: List<ReflectionEntry>
)

data class ReflectionsUiState(
    val isLoading: Boolean = true,
    val journeyId: String = "",
    val days: List<ReflectionDay> = emptyList()
)

@HiltViewModel
class ReflectionsViewModel @Inject constructor(
    private val journeyRepository: JourneyRepository,
    progressRepository: JourneyProgressRepository
) : ViewModel() {

    private val journey = journeyRepository.getJourney()

    val state: StateFlow<ReflectionsUiState> =
        progressRepository.allEntries(journey.id).map { entries ->
            val days = entries
                .groupBy { it.day }
                .toSortedMap()
                .map { (day, dayEntries) ->
                    ReflectionDay(
                        day = day,
                        title = journeyRepository.getDay(journey.id, day)?.title.orEmpty(),
                        entries = dayEntries
                            .map { ReflectionEntry(it.field, kindOf(it.field), it.text) }
                            .sortedBy { fieldOrder(it.field) }
                    )
                }
            ReflectionsUiState(isLoading = false, journeyId = journey.id, days = days)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ReflectionsUiState())

    private fun kindOf(field: String): ReflectionFieldKind = when {
        field == "prompt" -> ReflectionFieldKind.Prompt
        field.startsWith("deepDive") -> ReflectionFieldKind.DeepDive
        field == "evening" -> ReflectionFieldKind.Evening
        else -> ReflectionFieldKind.Note
    }

    /** Reading order within a day: prompt → deep dives → evening → notes. */
    private fun fieldOrder(field: String): Int = when {
        field == "prompt" -> 0
        field.startsWith("deepDive") -> 1 + (field.removePrefix("deepDive").toIntOrNull() ?: 0)
        field == "evening" -> 100
        else -> 200
    }
}
