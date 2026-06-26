package com.within.app.ui.journey

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.within.app.R
import com.within.app.ui.components.CompanionExpression
import com.within.app.ui.components.CompanionImage
import com.within.app.ui.journey.components.PhaseChip
import com.within.app.ui.theme.OnboardingBg
import com.within.app.ui.theme.OnboardingCard
import com.within.app.ui.theme.OnboardingText
import com.within.app.ui.theme.OnboardingTextSecondary
import com.within.app.ui.theme.Supportive

@Composable
fun ReflectionsScreen(
    onBack: () -> Unit,
    onOpenDay: (String, Int) -> Unit,
    viewModel: ReflectionsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OnboardingBg)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(Modifier.fillMaxSize()) {
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
                    text = stringResource(R.string.reflections_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = OnboardingText
                )
            }

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Supportive)
                }
                state.days.isEmpty() -> EmptyReflections()
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 22.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(state.days, key = { it.day }) { day ->
                        ReflectionDayCard(
                            day = day,
                            onClick = { onOpenDay(state.journeyId, day.day) }
                        )
                    }
                    item { Spacer(Modifier.height(20.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ReflectionDayCard(day: ReflectionDay, onClick: () -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, shape, ambientColor = Color(0x141B2A1B))
            .clip(shape)
            .background(Color.White)
            .border(1.5.dp, OnboardingCard, shape)
            .clickable(onClick = onClick)
            .padding(18.dp)
    ) {
        Text(
            text = stringResource(R.string.journey_day_label, day.day),
            style = MaterialTheme.typography.labelSmall,
            color = Supportive
        )
        Spacer(Modifier.height(2.dp))
        Text(text = day.title, style = MaterialTheme.typography.titleLarge, color = OnboardingText)
        day.entries.forEach { entry ->
            Spacer(Modifier.height(12.dp))
            PhaseChip(stringResource(labelFor(entry.kind)))
            Spacer(Modifier.height(6.dp))
            Text(
                text = entry.text,
                style = MaterialTheme.typography.bodyMedium,
                color = OnboardingTextSecondary
            )
        }
    }
}

private fun labelFor(kind: ReflectionFieldKind): Int = when (kind) {
    ReflectionFieldKind.Prompt -> R.string.reflections_field_prompt
    ReflectionFieldKind.DeepDive -> R.string.reflections_field_deepdive
    ReflectionFieldKind.Evening -> R.string.reflections_field_evening
    ReflectionFieldKind.Note -> R.string.reflections_field_note
}

@Composable
private fun EmptyReflections() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CompanionImage(expression = CompanionExpression.Thinking, height = 140.dp)
        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.reflections_empty_title),
            style = MaterialTheme.typography.headlineSmall,
            color = OnboardingText,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.reflections_empty_body),
            style = MaterialTheme.typography.bodyLarge,
            color = OnboardingTextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
