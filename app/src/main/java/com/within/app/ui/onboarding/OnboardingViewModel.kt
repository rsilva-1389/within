package com.within.app.ui.onboarding

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.within.app.R
import com.within.app.data.model.Category
import com.within.app.data.preferences.UserPreferences
import com.within.app.data.repository.JourneyProgressRepository
import com.within.app.data.repository.JourneyRepository
import com.within.app.notification.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

fun defaultTimesFor(count: Int): List<String> = when (count) {
    1 -> listOf("08:00")
    2 -> listOf("08:00", "18:00")
    3 -> listOf("08:00", "13:00", "20:00")
    4 -> listOf("07:00", "11:00", "16:00", "20:00")
    else -> listOf("07:00", "10:00", "13:00", "17:00", "21:00")
}

enum class TimeOfDay(val defaultTime: String) {
    Morning("08:00"),
    Noon("13:00"),
    Evening("20:00")
}

/** How the user says they're feeling right now. "Struggling" moods earn a gentle response from Ori. */
enum class Mood(
    @StringRes val labelRes: Int,
    val struggling: Boolean,
    @StringRes val responseRes: Int?
) {
    Light(R.string.onboarding_mood_light, false, null),
    Okay(R.string.onboarding_mood_okay, false, null),
    Tired(R.string.onboarding_mood_tired, true, R.string.onboarding_respond_tired),
    Anxious(R.string.onboarding_mood_anxious, true, R.string.onboarding_respond_anxious),
    Heavy(R.string.onboarding_mood_heavy, true, R.string.onboarding_respond_heavy),
    Numb(R.string.onboarding_mood_numb, true, R.string.onboarding_respond_numb)
}

/** Where the user wants the journey to take them — each maps onto one of the four stages. */
enum class DesiredFeeling(
    @StringRes val labelRes: Int,
    @StringRes val connectorRes: Int
) {
    Peace(R.string.onboarding_feeling_peace, R.string.onboarding_stage_connector_peace),
    Confident(R.string.onboarding_feeling_confident, R.string.onboarding_stage_connector_confident),
    Connected(R.string.onboarding_feeling_connected, R.string.onboarding_stage_connector_connected),
    Clear(R.string.onboarding_feeling_clear, R.string.onboarding_stage_connector_clear)
}

/** Step indices for the onboarding flow. Step [STEP_RESPOND] is skipped for non-struggling moods. */
object OnboardingSteps {
    const val COVER = 0
    const val INTRO = 1
    const val NAME = 2
    const val MOOD = 3
    const val RESPOND = 4
    const val FEELING = 5
    const val STAGES = 6
    const val DAY = 7
    const val PROMISES = 8
    const val REMINDER = 9
    const val ALL_SET = 10
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferences: UserPreferences,
    private val notificationScheduler: NotificationScheduler,
    private val journeyRepository: JourneyRepository,
    private val journeyProgressRepository: JourneyProgressRepository
) : ViewModel() {

    private val _step = MutableStateFlow(OnboardingSteps.COVER)
    val step = _step.asStateFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _mood = MutableStateFlow<Mood?>(null)
    val mood = _mood.asStateFlow()

    private val _desiredFeeling = MutableStateFlow<DesiredFeeling?>(null)
    val desiredFeeling = _desiredFeeling.asStateFlow()

    private val _timeOfDay = MutableStateFlow(TimeOfDay.Morning)
    val timeOfDay = _timeOfDay.asStateFlow()

    private val _notificationTime = MutableStateFlow("08:00")
    val notificationTime = _notificationTime.asStateFlow()

    private val isStruggling: Boolean get() = _mood.value?.struggling == true

    fun nextStep() {
        val current = _step.value
        _step.value = when {
            // Skip Ori's response screen unless the chosen mood calls for it.
            current == OnboardingSteps.MOOD && !isStruggling -> OnboardingSteps.FEELING
            else -> current + 1
        }
    }

    fun prevStep() {
        val current = _step.value
        if (current <= OnboardingSteps.COVER) return
        _step.value = when {
            current == OnboardingSteps.FEELING && !isStruggling -> OnboardingSteps.MOOD
            else -> current - 1
        }
    }

    fun setName(name: String) { _name.value = name }

    fun setMood(mood: Mood) { _mood.value = mood }

    fun setDesiredFeeling(feeling: DesiredFeeling) { _desiredFeeling.value = feeling }

    fun setTimeOfDay(tod: TimeOfDay) {
        _timeOfDay.value = tod
        _notificationTime.value = tod.defaultTime
    }

    fun setNotificationTime(time: String) { _notificationTime.value = time }

    fun completeOnboarding(onDone: () -> Unit) {
        viewModelScope.launch {
            userPreferences.setUserName(_name.value.trim())
            userPreferences.setMessagesPerDay(1)
            userPreferences.setNotificationTimes(listOf(_notificationTime.value))
            userPreferences.setEnabledCategories(Category.entries.toSet())
            // Begin the journey here so the user lands directly in Day 1 — no second "Begin" tap.
            val journeyId = journeyRepository.getJourney().id
            journeyProgressRepository.startJourney(journeyId)
            userPreferences.setActiveJourneyId(journeyId)
            userPreferences.setOnboardingCompleted()
            notificationScheduler.scheduleAll(listOf(_notificationTime.value))
            onDone()
        }
    }
}
