package com.within.app.ui.journey.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.within.app.R
import com.within.app.ui.theme.Calm
import com.within.app.ui.theme.Gentle
import com.within.app.ui.theme.OnboardingCard
import com.within.app.ui.theme.OnboardingDot
import com.within.app.ui.theme.OnboardingText
import com.within.app.ui.theme.OnboardingTextSecondary
import com.within.app.ui.theme.Positive
import com.within.app.ui.theme.Supportive
import com.within.app.ui.theme.Warm

private val CardShape = RoundedCornerShape(22.dp)
private val CardShadow = Color(0x141B2A1B)

/** Coral pill CTA, matching the onboarding button. */
@Composable
fun JourneyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Warm,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White,
            disabledContainerColor = color.copy(alpha = 0.4f),
            disabledContentColor = Color.White.copy(alpha = 0.6f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 4.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

/** Small rounded phase label, e.g. "SELF-ACCEPTANCE". */
@Composable
fun PhaseChip(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = Positive,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Calm.copy(alpha = 0.35f))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    )
}

/** Circular progress ring with two lines of centered text. */
@Composable
fun ProgressRing(
    fraction: Float,
    centerTop: String,
    centerBottom: String,
    modifier: Modifier = Modifier,
    diameter: Dp = 138.dp,
    ringColor: Color = Warm,
    trackColor: Color = OnboardingDot
) {
    Box(modifier = modifier.size(diameter), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(diameter)) {
            val strokeWidth = 12.dp.toPx()
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * fraction.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = centerTop, style = MaterialTheme.typography.headlineMedium, color = OnboardingText)
            Text(text = centerBottom, style = MaterialTheme.typography.labelMedium, color = OnboardingTextSecondary)
        }
    }
}

/** A peach + teal stat pill row: streak and days completed. */
@Composable
fun StatRow(streak: Int, completed: Int, total: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatPill(value = "🔥 $streak", label = stringResource(R.string.journey_stat_streak), tint = Supportive)
        StatPill(value = "$completed/$total", label = stringResource(R.string.journey_stat_completed), tint = Positive)
    }
}

@Composable
private fun StatPill(value: String, label: String, tint: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(tint.copy(alpha = 0.16f))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = OnboardingText)
        Text(label, style = MaterialTheme.typography.labelMedium, color = OnboardingTextSecondary)
    }
}

/** White card surfacing the current day; tap to open it. */
@Composable
fun DayCard(
    label: String,
    dayNumber: Int,
    title: String,
    theme: String,
    completed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, CardShape, ambientColor = CardShadow)
            .clip(CardShape)
            .background(Color.White)
            .border(1.5.dp, OnboardingCard, CardShape)
            .clickable(onClick = onClick)
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$label · DAY $dayNumber",
                style = MaterialTheme.typography.labelSmall,
                color = Supportive
            )
            Spacer(Modifier.height(6.dp))
            Text(text = title, style = MaterialTheme.typography.titleLarge, color = OnboardingText)
            Spacer(Modifier.height(2.dp))
            Text(text = theme, style = MaterialTheme.typography.bodyMedium, color = OnboardingTextSecondary)
        }
        if (completed) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Positive, modifier = Modifier.size(26.dp))
        }
    }
}

/** A titled, collapsible white card used for each section of a day. */
@Composable
fun JourneySection(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    collapsible: Boolean = true,
    initiallyExpanded: Boolean = true,
    accent: Color = Warm,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by rememberSaveable(title) { mutableStateOf(initiallyExpanded) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, CardShape, ambientColor = CardShadow)
            .clip(CardShape)
            .background(Color.White)
            .border(1.5.dp, OnboardingCard, CardShape)
            .padding(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .then(if (collapsible) Modifier.clickable { expanded = !expanded } else Modifier)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleLarge, color = OnboardingText)
                if (subtitle != null) {
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = OnboardingTextSecondary)
                }
            }
            if (collapsible) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = accent
                )
            }
        }
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(top = 12.dp), content = content)
        }
    }
}

/** Checkable row for the day's mindful action. */
@Composable
fun MindfulActionRow(
    text: String,
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(if (checked) Calm.copy(alpha = 0.30f) else Gentle.copy(alpha = 0.45f))
            .border(1.5.dp, OnboardingCard, CardShape)
            .clickable(onClick = onToggle)
            .padding(18.dp)
    ) {
        Icon(
            imageVector = if (checked) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
            contentDescription = null,
            tint = if (checked) Positive else OnboardingTextSecondary,
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = OnboardingText,
            modifier = Modifier.weight(1f)
        )
    }
}

/** Centered completed badge shown once the day is done. */
@Composable
fun CompletedBadge(text: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(Positive.copy(alpha = 0.16f)),
        // center contents
    ) {
        Spacer(Modifier.width(0.dp))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.CheckCircle, null, tint = Positive, modifier = Modifier.size(22.dp))
                Text(text, style = MaterialTheme.typography.labelLarge, color = Positive)
            }
        }
    }
}

/** Small circular avatar placeholder kept for reuse. */
@Composable
internal fun Dot(color: Color, size: Dp = 8.dp) {
    Box(modifier = Modifier.size(size).clip(CircleShape).background(color))
}
