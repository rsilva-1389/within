package com.within.app.ui.journey.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.within.app.R
import com.within.app.billing.Pricing
import com.within.app.ui.components.CompanionExpression
import com.within.app.ui.components.CompanionImage
import com.within.app.ui.theme.OnboardingBg
import com.within.app.ui.theme.OnboardingDot
import com.within.app.ui.theme.OnboardingText
import com.within.app.ui.theme.OnboardingTextSecondary
import com.within.app.ui.theme.Positive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallSheet(
    onDismiss: () -> Unit,
    onSelectPlan: (planId: String) -> Unit,
    annualPrice: String,
    monthlyPrice: String,
    trialDays: Int
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = OnboardingBg,
        dragHandle = { BottomSheetDefaults.DragHandle(color = OnboardingDot) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 26.dp)
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CompanionImage(expression = CompanionExpression.Warmheart, height = 104.dp)
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.paywall_title),
                style = MaterialTheme.typography.headlineMedium,
                color = OnboardingText
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.paywall_body),
                style = MaterialTheme.typography.bodyMedium,
                color = OnboardingTextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(18.dp))
            PerkRow(stringResource(R.string.paywall_perk_1))
            PerkRow(stringResource(R.string.paywall_perk_2))
            PerkRow(stringResource(R.string.paywall_perk_3))
            Spacer(Modifier.height(20.dp))
            JourneyButton(
                text = stringResource(R.string.paywall_cta, trialDays),
                onClick = { onSelectPlan(Pricing.ANNUAL_PLAN_ID) }
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.paywall_annual_note, annualPrice),
                style = MaterialTheme.typography.bodySmall,
                color = OnboardingTextSecondary
            )
            Spacer(Modifier.height(10.dp))
            TextButton(onClick = { onSelectPlan(Pricing.MONTHLY_PLAN_ID) }) {
                Text(
                    text = stringResource(R.string.paywall_monthly_cta, monthlyPrice),
                    style = MaterialTheme.typography.labelLarge,
                    color = Positive
                )
            }
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.paywall_dismiss),
                    style = MaterialTheme.typography.labelLarge,
                    color = OnboardingTextSecondary
                )
            }
        }
    }
}

@Composable
private fun PerkRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Positive, modifier = Modifier.size(20.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge, color = OnboardingText, modifier = Modifier.weight(1f))
    }
}
