package com.within.app.ui.journey.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.within.app.R
import com.within.app.ui.components.CompanionExpression
import com.within.app.ui.components.CompanionImage
import com.within.app.ui.theme.OnboardingBg
import com.within.app.ui.theme.OnboardingText
import com.within.app.ui.theme.OnboardingTextSecondary
import com.within.app.ui.theme.Positive

/**
 * The moment right after a day is marked complete: Ori in gratitude, the streak, and a warm line.
 * Surfaces the final-day message when the whole journey is finished.
 */
@Composable
fun CelebrationOverlay(
    day: Int,
    streak: Int,
    journeyComplete: Boolean,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(OnboardingBg)
                .padding(horizontal = 26.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CompanionImage(expression = CompanionExpression.Gratitude, height = 150.dp)
            Spacer(Modifier.height(18.dp))
            Text(
                text = if (journeyComplete)
                    stringResource(R.string.celebration_journey_done)
                else
                    stringResource(R.string.celebration_title, day),
                style = MaterialTheme.typography.headlineSmall,
                color = OnboardingText,
                textAlign = TextAlign.Center
            )
            if (streak > 1) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.celebration_streak, streak),
                    style = MaterialTheme.typography.titleMedium,
                    color = Positive
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.celebration_body),
                style = MaterialTheme.typography.bodyLarge,
                color = OnboardingTextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            JourneyButton(
                text = stringResource(R.string.celebration_cta),
                onClick = onDismiss,
                color = Positive
            )
        }
    }
}
