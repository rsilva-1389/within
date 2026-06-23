package com.within.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.within.app.data.model.Category
import com.within.app.data.preferences.UserPreferences
import com.within.app.notification.NotificationScheduler
import com.within.app.ui.onboarding.defaultTimesFor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val messagesPerDay: Int = 1,
    val notificationTimes: List<String> = listOf("08:00"),
    val enabledCategories: Set<Category> = Category.entries.toSet()
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        userPreferences.messagesPerDay,
        userPreferences.notificationTimes,
        userPreferences.enabledCategories
    ) { count, times, categories ->
        SettingsUiState(count, times, categories)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun setMessagesPerDay(count: Int) {
        viewModelScope.launch {
            val times = defaultTimesFor(count)
            userPreferences.setMessagesPerDay(count)
            userPreferences.setNotificationTimes(times)
            notificationScheduler.scheduleAll(times)
        }
    }

    fun setTimeAt(index: Int, time: String) {
        viewModelScope.launch {
            val times = uiState.value.notificationTimes.toMutableList().also { it[index] = time }
            userPreferences.setNotificationTimes(times)
            notificationScheduler.scheduleAll(times)
        }
    }

    fun toggleCategory(category: Category) {
        val current = uiState.value.enabledCategories.toMutableSet()
        if (category in current && current.size > 1) current.remove(category) else current.add(category)
        viewModelScope.launch { userPreferences.setEnabledCategories(current) }
    }
}
