package com.within.app.ui.journey

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.within.app.billing.BillingManager
import com.within.app.billing.Pricing
import com.within.app.data.preferences.UserPreferences
import com.within.app.data.repository.JourneyProgressRepository
import com.within.app.data.repository.JourneyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JourneyDayUiState(
    val isLoading: Boolean = true,
    val day: Int = 1,
    val title: String = "",
    val theme: String = "",
    val phaseTitle: String = "",
    val reflection: List<String> = emptyList(),
    val prompt: String = "",
    val deepDive: List<String> = emptyList(),
    val reaffirmation: String = "",
    val mindfulAction: String = "",
    val eveningReflection: String = "",
    val fieldTexts: Map<String, String> = emptyMap(),
    val mindfulActionDone: Boolean = false,
    val completed: Boolean = false,
    val isPremium: Boolean = false,
    val canAccess: Boolean = true,
    /** Live prices from Play, falling back to placeholder copy until they load. */
    val annualPrice: String = Pricing.ANNUAL_PRICE,
    val monthlyPrice: String = Pricing.MONTHLY_PRICE,
    /** One-shot celebration shown right after the user marks the day complete. */
    val celebration: Celebration? = null
)

/** Payload for the post-completion moment. [journeyComplete] is true on the final day. */
data class Celebration(val day: Int, val streak: Int, val journeyComplete: Boolean)

@HiltViewModel
class JourneyDayViewModel @Inject constructor(
    private val journeyRepository: JourneyRepository,
    private val progressRepository: JourneyProgressRepository,
    private val userPreferences: UserPreferences,
    private val billingManager: BillingManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val journeyId: String = checkNotNull(savedStateHandle.get<String>("journeyId"))
    private val day: Int = checkNotNull(savedStateHandle.get<Int>("day"))

    private val _state = MutableStateFlow(buildInitialState())
    val state = _state.asStateFlow()

    private fun buildInitialState(): JourneyDayUiState {
        val content = journeyRepository.getDay(journeyId, day)
        val phase = journeyRepository.phaseForDay(journeyId, day)
        return JourneyDayUiState(
            isLoading = true,
            day = day,
            title = content?.title.orEmpty(),
            theme = content?.theme.orEmpty(),
            phaseTitle = phase?.title.orEmpty(),
            reflection = content?.reflection.orEmpty(),
            prompt = content?.prompt.orEmpty(),
            deepDive = content?.deepDive.orEmpty(),
            reaffirmation = content?.reaffirmation.orEmpty(),
            mindfulAction = content?.mindfulAction.orEmpty(),
            eveningReflection = content?.eveningReflection.orEmpty()
        )
    }

    init {
        viewModelScope.launch {
            val entries = progressRepository.entriesFor(journeyId, day).first()
            _state.update { it.copy(fieldTexts = entries.associate { e -> e.field to e.text }, isLoading = false) }
        }
        viewModelScope.launch {
            progressRepository.dayProgress(journeyId, day).collect { dp ->
                _state.update {
                    it.copy(completed = dp?.completed == true, mindfulActionDone = dp?.mindfulActionDone == true)
                }
            }
        }
        viewModelScope.launch {
            userPreferences.isPremium.collect { premium ->
                _state.update { it.copy(isPremium = premium, canAccess = journeyRepository.canAccessDay(journeyId, day, premium)) }
            }
        }
        viewModelScope.launch {
            billingManager.planPrices.collect { prices ->
                _state.update {
                    it.copy(
                        annualPrice = prices[Pricing.ANNUAL_PLAN_ID] ?: it.annualPrice,
                        monthlyPrice = prices[Pricing.MONTHLY_PLAN_ID] ?: it.monthlyPrice
                    )
                }
            }
        }
    }

    /** Autosave a journaling field; updates state optimistically, persists to Room. */
    fun saveEntry(field: String, text: String) {
        _state.update { it.copy(fieldTexts = it.fieldTexts.toMutableMap().apply { put(field, text) }) }
        viewModelScope.launch { progressRepository.saveEntry(journeyId, day, field, text) }
    }

    fun toggleMindfulAction() {
        val next = !_state.value.mindfulActionDone
        viewModelScope.launch { progressRepository.setMindfulActionDone(journeyId, day, next) }
    }

    fun markComplete() {
        viewModelScope.launch {
            progressRepository.markCompleted(journeyId, day, true)
            val streak = progressRepository.currentStreak(journeyId).first()
            val completedCount = progressRepository.completedDays(journeyId).first().size
            val lengthDays = journeyRepository.getJourney(journeyId)?.lengthDays ?: 0
            val journeyComplete = lengthDays > 0 && completedCount >= lengthDays
            _state.update {
                it.copy(celebration = Celebration(day, streak, journeyComplete))
            }
        }
    }

    fun dismissCelebration() {
        _state.update { it.copy(celebration = null) }
    }

    fun recordPaywallTap() {
        viewModelScope.launch { userPreferences.recordPaywallTap() }
    }

    /** Launch the Play purchase flow for the chosen base plan. Entitlement updates via BillingManager. */
    fun startPurchase(activity: Activity, planId: String) {
        billingManager.launchPurchase(activity, planId)
    }
}
