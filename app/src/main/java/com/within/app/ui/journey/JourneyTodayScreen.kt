package com.within.app.ui.journey

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.within.app.R
import com.within.app.ui.components.CompanionExpression
import com.within.app.ui.components.CompanionImage
import com.within.app.ui.journey.components.DayCard
import com.within.app.ui.journey.components.JourneyButton
import com.within.app.ui.journey.components.PhaseChip
import com.within.app.ui.journey.components.ProgressRing
import com.within.app.ui.journey.components.StatRow
import com.within.app.ui.theme.OnboardingBg
import com.within.app.ui.theme.OnboardingText
import com.within.app.ui.theme.OnboardingTextSecondary
import com.within.app.ui.theme.Positive
import com.within.app.ui.theme.Warm

@Composable
fun JourneyTodayScreen(
    onNavigateToSettings: () -> Unit,
    onOpenDay: (String, Int) -> Unit,
    onOpenMap: () -> Unit,
    onOpenReflections: () -> Unit,
    viewModel: JourneyTodayViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OnboardingBg)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        IconButton(
            onClick = onNavigateToSettings,
            modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(R.string.journey_settings),
                tint = OnboardingTextSecondary
            )
        }

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Warm)
            }
            !state.started -> JourneyIntro(state = state, onBegin = viewModel::startJourney)
            else -> JourneyActive(
                state = state,
                onOpenDay = onOpenDay,
                onOpenMap = onOpenMap,
                onOpenReflections = onOpenReflections
            )
        }
    }
}

@Composable
private fun JourneyIntro(state: JourneyTodayUiState, onBegin: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 26.dp)
            .padding(bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            CompanionImage(expression = CompanionExpression.Warmheart, height = 200.dp)
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(state.title, style = MaterialTheme.typography.headlineLarge, color = OnboardingText)
            Spacer(Modifier.height(8.dp))
            Text(state.subtitle, style = MaterialTheme.typography.titleLarge, color = Positive)
            Spacer(Modifier.height(12.dp))
            Text(state.tagline, style = MaterialTheme.typography.bodyLarge, color = OnboardingTextSecondary)
            Spacer(Modifier.height(26.dp))
            JourneyButton(text = stringResource(R.string.journey_intro_cta), onClick = onBegin)
        }
    }
}

@Composable
private fun JourneyActive(
    state: JourneyTodayUiState,
    onOpenDay: (String, Int) -> Unit,
    onOpenMap: () -> Unit,
    onOpenReflections: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 26.dp)
            .padding(top = 52.dp, bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CompanionImage(expression = CompanionExpression.Thinking, height = 120.dp)
        Spacer(Modifier.height(10.dp))
        Text(
            text = state.title,
            style = MaterialTheme.typography.headlineSmall,
            color = OnboardingText,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        ProgressRing(
            fraction = state.availableDay.toFloat() / state.totalDays,
            centerTop = stringResource(R.string.journey_day_label, state.availableDay),
            centerBottom = stringResource(R.string.journey_progress_of, state.totalDays)
        )
        Spacer(Modifier.height(16.dp))
        StatRow(streak = state.streak, completed = state.completedCount, total = state.totalDays)
        Spacer(Modifier.height(24.dp))
        PhaseChip(state.phaseTitle, modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(10.dp))
        DayCard(
            label = stringResource(R.string.journey_today_label),
            dayNumber = state.focusDay,
            title = state.focusTitle,
            theme = state.focusTheme,
            completed = state.focusCompleted,
            onClick = { onOpenDay(state.journeyId, state.focusDay) }
        )
        Spacer(Modifier.height(18.dp))
        JourneyButton(
            text = if (state.focusCompleted)
                stringResource(R.string.journey_revisit_cta, state.focusDay)
            else
                stringResource(R.string.journey_continue_cta, state.focusDay),
            onClick = { onOpenDay(state.journeyId, state.focusDay) }
        )
        Spacer(Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            NavLink(
                text = stringResource(R.string.journey_map_link),
                onClick = onOpenMap,
                modifier = Modifier.weight(1f)
            )
            NavLink(
                text = stringResource(R.string.journey_reflections_link),
                onClick = onOpenReflections,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/** Quiet text link beneath the main CTA — opens the map / reflections. */
@Composable
private fun NavLink(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = Positive,
        textAlign = TextAlign.Center,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
    )
}
