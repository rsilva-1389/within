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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.within.app.R
import com.within.app.ui.journey.components.PhaseChip
import com.within.app.ui.theme.OnboardingBg
import com.within.app.ui.theme.OnboardingCard
import com.within.app.ui.theme.OnboardingText
import com.within.app.ui.theme.OnboardingTextSecondary
import com.within.app.ui.theme.Positive
import com.within.app.ui.theme.Warm

@Composable
fun JourneyMapScreen(
    onBack: () -> Unit,
    onOpenDay: (String, Int) -> Unit,
    viewModel: JourneyMapViewModel = hiltViewModel()
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
                    text = stringResource(R.string.journey_map_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = OnboardingText
                )
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Warm)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 22.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(
                                R.string.journey_map_subtitle,
                                state.completedCount,
                                state.totalDays
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            color = OnboardingTextSecondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                    state.phases.forEach { phase ->
                        item(key = "phase-${phase.id}") {
                            PhaseChip(
                                phase.title,
                                modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)
                            )
                        }
                        items(phase.days, key = { "day-${it.day}" }) { mapDay ->
                            MapDayRow(
                                mapDay = mapDay,
                                onClick = { onOpenDay(state.journeyId, mapDay.day) }
                            )
                        }
                    }
                    item { Spacer(Modifier.height(20.dp)) }
                }
            }
        }
    }
}

@Composable
private fun MapDayRow(mapDay: MapDay, onClick: () -> Unit) {
    val locked = mapDay.status == DayStatus.Locked
    val shape = RoundedCornerShape(18.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (locked) Color.Transparent else Color.White)
            .border(1.5.dp, OnboardingCard, shape)
            .then(if (locked) Modifier else Modifier.clickable(onClick = onClick))
            .padding(14.dp)
    ) {
        DayBadge(mapDay)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (locked)
                    stringResource(R.string.journey_day_locked_hint, mapDay.day)
                else
                    stringResource(R.string.journey_day_label, mapDay.day),
                style = MaterialTheme.typography.labelSmall,
                color = OnboardingTextSecondary
            )
            Text(
                text = mapDay.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (locked) OnboardingTextSecondary else OnboardingText
            )
            if (!locked) {
                Text(
                    text = mapDay.theme,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnboardingTextSecondary
                )
            }
        }
    }
}

@Composable
private fun DayBadge(mapDay: MapDay) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(
                when (mapDay.status) {
                    DayStatus.Completed -> Positive.copy(alpha = 0.16f)
                    DayStatus.Available -> Warm.copy(alpha = 0.16f)
                    DayStatus.Locked -> OnboardingCard
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        when (mapDay.status) {
            DayStatus.Completed -> Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Positive,
                modifier = Modifier.size(22.dp)
            )
            DayStatus.Available -> Text(
                text = "${mapDay.day}",
                style = MaterialTheme.typography.titleMedium,
                color = Warm
            )
            DayStatus.Locked -> Icon(
                Icons.Filled.Lock,
                contentDescription = null,
                tint = OnboardingTextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
