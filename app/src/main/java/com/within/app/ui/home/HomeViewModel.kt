package com.within.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.within.app.data.model.Message
import com.within.app.data.preferences.UserPreferences
import com.within.app.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val message: Message? = null,
    val isFavorite: Boolean = false,
    val streak: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMessage()
        observeFavoriteState()
        observeStreak()
    }

    private fun loadMessage() {
        viewModelScope.launch {
            val categories = userPreferences.enabledCategories.first()
            val shownIds = userPreferences.shownMessageIds.first()
            val message = messageRepository.getRandomMessage(categories, shownIds)
            if (message != null) userPreferences.recordShownMessage(message.id)
            _uiState.update { it.copy(message = message, isLoading = false) }
        }
    }

    private fun observeFavoriteState() {
        viewModelScope.launch {
            combine(
                _uiState.map { it.message?.id }.distinctUntilChanged(),
                userPreferences.favoriteMessageIds
            ) { id, favorites -> id != null && id in favorites }
                .collect { isFav -> _uiState.update { it.copy(isFavorite = isFav) } }
        }
    }

    private fun observeStreak() {
        viewModelScope.launch {
            userPreferences.streakCount.collect { streak ->
                _uiState.update { it.copy(streak = streak) }
            }
        }
    }

    fun toggleFavorite() {
        val id = _uiState.value.message?.id ?: return
        viewModelScope.launch { userPreferences.toggleFavorite(id) }
    }

    fun refreshMessage() {
        _uiState.update { it.copy(isLoading = true) }
        loadMessage()
    }
}
