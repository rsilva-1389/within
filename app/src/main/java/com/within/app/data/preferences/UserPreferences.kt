package com.within.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.within.app.data.model.Category
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "within_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val store = context.dataStore

    companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val USER_NAME = stringPreferencesKey("user_name")
        val MESSAGES_PER_DAY = intPreferencesKey("messages_per_day")
        val NOTIFICATION_TIMES = stringPreferencesKey("notification_times")
        val ENABLED_CATEGORIES = stringPreferencesKey("enabled_categories")
        val SHOWN_MESSAGE_IDS = stringPreferencesKey("shown_message_ids")
        val FAVORITE_MESSAGE_IDS = stringPreferencesKey("favorite_message_ids")
        val STREAK_COUNT = intPreferencesKey("streak_count")
        val LAST_INTERACTION_DATE = stringPreferencesKey("last_interaction_date")
    }

    val isOnboardingCompleted: Flow<Boolean> = store.data.map { it[ONBOARDING_COMPLETED] ?: false }

    val userName: Flow<String> = store.data.map { it[USER_NAME] ?: "" }

    val messagesPerDay: Flow<Int> = store.data.map { it[MESSAGES_PER_DAY] ?: 1 }

    val notificationTimes: Flow<List<String>> = store.data.map { prefs ->
        prefs[NOTIFICATION_TIMES]?.split(",")?.filter { it.isNotBlank() } ?: listOf("08:00")
    }

    val enabledCategories: Flow<Set<Category>> = store.data.map { prefs ->
        val saved = prefs[ENABLED_CATEGORIES]
        if (saved.isNullOrBlank()) Category.entries.toSet()
        else saved.split(",").mapNotNull { key -> Category.entries.find { it.key == key } }.toSet()
    }

    val favoriteMessageIds: Flow<Set<String>> = store.data.map { prefs ->
        prefs[FAVORITE_MESSAGE_IDS]?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
    }

    val shownMessageIds: Flow<Set<String>> = store.data.map { prefs ->
        prefs[SHOWN_MESSAGE_IDS]?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
    }

    val streakCount: Flow<Int> = store.data.map { it[STREAK_COUNT] ?: 0 }

    suspend fun setOnboardingCompleted() {
        store.edit { it[ONBOARDING_COMPLETED] = true }
    }

    suspend fun setUserName(name: String) {
        store.edit { it[USER_NAME] = name }
    }

    suspend fun setMessagesPerDay(count: Int) {
        store.edit { it[MESSAGES_PER_DAY] = count.coerceIn(1, 5) }
    }

    suspend fun setNotificationTimes(times: List<String>) {
        store.edit { it[NOTIFICATION_TIMES] = times.joinToString(",") }
    }

    suspend fun setEnabledCategories(categories: Set<Category>) {
        store.edit { it[ENABLED_CATEGORIES] = categories.joinToString(",") { c -> c.key } }
    }

    suspend fun toggleFavorite(messageId: String) {
        store.edit { prefs ->
            val current = prefs[FAVORITE_MESSAGE_IDS]
                ?.split(",")?.filter { it.isNotBlank() }?.toMutableSet() ?: mutableSetOf()
            if (messageId in current) current.remove(messageId) else current.add(messageId)
            prefs[FAVORITE_MESSAGE_IDS] = current.joinToString(",")
        }
    }

    suspend fun recordShownMessage(messageId: String) {
        store.edit { prefs ->
            val current = prefs[SHOWN_MESSAGE_IDS]
                ?.split(",")?.filter { it.isNotBlank() }?.toMutableList() ?: mutableListOf()
            current.add(messageId)
            if (current.size > 50) current.removeAt(0)
            prefs[SHOWN_MESSAGE_IDS] = current.joinToString(",")
        }
    }

    suspend fun updateStreak(todayDate: String) {
        store.edit { prefs ->
            val last = prefs[LAST_INTERACTION_DATE]
            val streak = prefs[STREAK_COUNT] ?: 0
            prefs[LAST_INTERACTION_DATE] = todayDate
            prefs[STREAK_COUNT] = if (last == null) 1 else streak + 1
        }
    }
}
