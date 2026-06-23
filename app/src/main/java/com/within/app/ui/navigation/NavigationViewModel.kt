package com.within.app.ui.navigation

import androidx.lifecycle.ViewModel
import com.within.app.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    preferences: UserPreferences
) : ViewModel() {
    val onboardingCompleted = preferences.isOnboardingCompleted
}
