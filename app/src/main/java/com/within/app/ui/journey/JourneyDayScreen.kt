package com.within.app.ui.journey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.within.app.share.shareReaffirmation
import com.within.app.util.findActivity
import com.within.app.R
import com.within.app.billing.Pricing
import com.within.app.ui.components.CompanionExpression
import com.within.app.ui.components.CompanionImage
import com.within.app.ui.journey.components.CelebrationOverlay
import com.within.app.ui.journey.components.CompletedBadge
import com.within.app.ui.journey.components.JournalField
import com.within.app.ui.journey.components.JourneyButton
import com.within.app.ui.journey.components.JourneySection
import com.within.app.ui.journey.components.MindfulActionRow
import com.within.app.ui.journey.components.PaywallSheet
import com.within.app.ui.journey.components.PhaseChip
import com.within.app.ui.journey.components.ReaffirmationCard
import com.within.app.ui.theme.OnboardingBg
import com.within.app.ui.theme.OnboardingText
import com.within.app.ui.theme.OnboardingTextSecondary

@Composable
fun JourneyDayScreen(
    onBack: () -> Unit,
    viewModel: JourneyDayViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showPaywall by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(state.canAccess) {
        if (!state.canAccess) viewModel.recordPaywallTap()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OnboardingBg)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            DayTopBar(day = state.day, phaseTitle = state.phaseTitle, onBack = onBack)
            if (!state.canAccess) {
                LockedDay(onUnlock = { showPaywall = true })
            } else {
                DayContent(state = state, viewModel = viewModel)
            }
        }
    }

    if (showPaywall) {
        PaywallSheet(
            onDismiss = { showPaywall = false },
            onSelectPlan = { planId ->
                context.findActivity()?.let { viewModel.startPurchase(it, planId) }
                showPaywall = false
            },
            annualPrice = state.annualPrice,
            monthlyPrice = state.monthlyPrice,
            trialDays = Pricing.TRIAL_DAYS
        )
    }

    state.celebration?.let { celebration ->
        CelebrationOverlay(
            day = celebration.day,
            streak = celebration.streak,
            journeyComplete = celebration.journeyComplete,
            onDismiss = viewModel::dismissCelebration
        )
    }
}

@Composable
private fun DayTopBar(day: Int, phaseTitle: String, onBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = OnboardingText)
        }
        Text(
            text = stringResource(R.string.journey_day_label, day),
            style = MaterialTheme.typography.titleLarge,
            color = OnboardingText,
            modifier = Modifier.weight(1f)
        )
        if (phaseTitle.isNotEmpty()) PhaseChip(phaseTitle)
        Spacer(Modifier.width(8.dp))
    }
}

@Composable
private fun DayContent(state: JourneyDayUiState, viewModel: JourneyDayViewModel) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 22.dp)
            .padding(top = 4.dp, bottom = 28.dp)
    ) {
        Text(state.title, style = MaterialTheme.typography.headlineMedium, color = OnboardingText)
        Text(state.theme, style = MaterialTheme.typography.bodyLarge, color = OnboardingTextSecondary)
        Spacer(Modifier.height(18.dp))

        JourneySection(title = stringResource(R.string.journey_section_reflection)) {
            state.reflection.forEachIndexed { index, paragraph ->
                if (index > 0) Spacer(Modifier.height(10.dp))
                Text(paragraph, style = MaterialTheme.typography.bodyLarge, color = OnboardingText)
            }
        }
        Spacer(Modifier.height(14.dp))

        JourneySection(title = stringResource(R.string.journey_section_prompt)) {
            Text(state.prompt, style = MaterialTheme.typography.titleMedium, color = OnboardingText)
            Spacer(Modifier.height(12.dp))
            JournalField(
                value = state.fieldTexts["prompt"].orEmpty(),
                onValueChange = { viewModel.saveEntry("prompt", it) },
                placeholder = stringResource(R.string.journey_journal_placeholder)
            )
        }
        Spacer(Modifier.height(14.dp))

        if (state.deepDive.isNotEmpty()) {
            JourneySection(
                title = stringResource(R.string.journey_section_deepdive),
                subtitle = stringResource(R.string.journey_section_deepdive_hint),
                initiallyExpanded = false
            ) {
                state.deepDive.forEachIndexed { index, question ->
                    if (index > 0) Spacer(Modifier.height(16.dp))
                    Text(question, style = MaterialTheme.typography.titleMedium, color = OnboardingText)
                    Spacer(Modifier.height(10.dp))
                    JournalField(
                        value = state.fieldTexts["deepDive$index"].orEmpty(),
                        onValueChange = { viewModel.saveEntry("deepDive$index", it) },
                        placeholder = stringResource(R.string.journey_journal_placeholder)
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        ReaffirmationCard(
            text = state.reaffirmation,
            label = stringResource(R.string.journey_reaffirmation_label),
            onShare = { shareReaffirmation(context, state.reaffirmation) }
        )
        Spacer(Modifier.height(18.dp))

        Text(
            text = stringResource(R.string.journey_mindful_action),
            style = MaterialTheme.typography.labelLarge,
            color = OnboardingText,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        MindfulActionRow(
            text = state.mindfulAction,
            checked = state.mindfulActionDone,
            onToggle = viewModel::toggleMindfulAction
        )
        Spacer(Modifier.height(14.dp))

        JourneySection(title = stringResource(R.string.journey_section_evening), initiallyExpanded = false) {
            Text(state.eveningReflection, style = MaterialTheme.typography.titleMedium, color = OnboardingText)
            Spacer(Modifier.height(12.dp))
            JournalField(
                value = state.fieldTexts["evening"].orEmpty(),
                onValueChange = { viewModel.saveEntry("evening", it) },
                placeholder = stringResource(R.string.journey_journal_placeholder)
            )
        }
        Spacer(Modifier.height(22.dp))

        if (state.completed) {
            CompletedBadge(text = stringResource(R.string.journey_completed))
        } else {
            JourneyButton(text = stringResource(R.string.journey_mark_complete), onClick = viewModel::markComplete)
        }
    }
}

@Composable
private fun LockedDay(onUnlock: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 26.dp)
            .padding(bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CompanionImage(expression = CompanionExpression.Warmheart, height = 160.dp)
                Spacer(Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.journey_locked_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = OnboardingText,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.journey_locked_body),
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnboardingTextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
        JourneyButton(text = stringResource(R.string.journey_unlock_cta), onClick = onUnlock)
    }
}
