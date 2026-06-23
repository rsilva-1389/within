package com.within.app.ui.onboarding

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.within.app.data.model.Category
import com.within.app.data.preferences.UserPreferences
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

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferences: UserPreferences,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    private val _step = MutableStateFlow(0)
    val step = _step.asStateFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _timeOfDay = MutableStateFlow(TimeOfDay.Morning)
    val timeOfDay = _timeOfDay.asStateFlow()

    private val _notificationTime = MutableStateFlow("08:00")
    val notificationTime = _notificationTime.asStateFlow()

    fun nextStep() { _step.value++ }
    fun prevStep() { if (_step.value > 0) _step.value-- }

    fun setName(name: String) { _name.value = name }

    fun setTimeOfDay(tod: TimeOfDay) {
        _timeOfDay.value = tod
        _notificationTime.value = tod.defaultTime
    }

    fun setNotificationTime(time: String) { _notificationTime.value = time }

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferences.setUserName(_name.value.trim())
            userPreferences.setMessagesPerDay(1)
            userPreferences.setNotificationTimes(listOf(_notificationTime.value))
            userPreferences.setEnabledCategories(Category.entries.toSet())
            userPreferences.setOnboardingCompleted()
            notificationScheduler.scheduleAll(listOf(_notificationTime.value))
        }
    }
}
