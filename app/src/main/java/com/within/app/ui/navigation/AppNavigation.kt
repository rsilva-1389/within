package com.within.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.within.app.ui.journey.JourneyDayScreen
import com.within.app.ui.journey.JourneyMapScreen
import com.within.app.ui.journey.JourneyTodayScreen
import com.within.app.ui.journey.ReflectionsScreen
import com.within.app.ui.onboarding.OnboardingScreen
import com.within.app.ui.settings.SettingsScreen

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object JourneyToday : Screen("journey")
    data object JourneyMap : Screen("journey/map")
    data object Reflections : Screen("journey/reflections")
    data object Settings : Screen("settings")
    data object JourneyDay : Screen("journey/{journeyId}/day/{day}") {
        fun route(journeyId: String, day: Int) = "journey/$journeyId/day/$day"
    }
}

@Composable
fun AppNavigation(viewModel: NavigationViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val onboardingCompleted by viewModel.onboardingCompleted.collectAsState(initial = null)

    if (onboardingCompleted == null) return

    NavHost(
        navController = navController,
        startDestination = if (onboardingCompleted == true) Screen.JourneyToday.route else Screen.Onboarding.route
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(onFinished = {
                navController.navigate(Screen.JourneyToday.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }
        composable(Screen.JourneyToday.route) {
            JourneyTodayScreen(
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onOpenDay = { journeyId, day ->
                    navController.navigate(Screen.JourneyDay.route(journeyId, day))
                },
                onOpenMap = { navController.navigate(Screen.JourneyMap.route) },
                onOpenReflections = { navController.navigate(Screen.Reflections.route) }
            )
        }
        composable(Screen.JourneyMap.route) {
            JourneyMapScreen(
                onBack = { navController.popBackStack() },
                onOpenDay = { journeyId, day ->
                    navController.navigate(Screen.JourneyDay.route(journeyId, day))
                }
            )
        }
        composable(Screen.Reflections.route) {
            ReflectionsScreen(
                onBack = { navController.popBackStack() },
                onOpenDay = { journeyId, day ->
                    navController.navigate(Screen.JourneyDay.route(journeyId, day))
                }
            )
        }
        composable(
            route = Screen.JourneyDay.route,
            arguments = listOf(
                navArgument("journeyId") { type = NavType.StringType },
                navArgument("day") { type = NavType.IntType }
            ),
            deepLinks = listOf(navDeepLink { uriPattern = "within://journey/{journeyId}/day/{day}" })
        ) {
            JourneyDayScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
