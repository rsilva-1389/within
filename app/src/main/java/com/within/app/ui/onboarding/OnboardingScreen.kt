package com.within.app.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.asImageBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.within.app.R
import com.within.app.ui.theme.*

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val step by viewModel.step.collectAsState()
    val name by viewModel.name.collectAsState()
    val timeOfDay by viewModel.timeOfDay.collectAsState()
    val notificationTime by viewModel.notificationTime.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        viewModel.completeOnboarding()
        onFinished()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = step,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { it } + fadeIn() togetherWith
                        slideOutHorizontally { -it } + fadeOut()
                } else {
                    slideInHorizontally { -it } + fadeIn() togetherWith
                        slideOutHorizontally { it } + fadeOut()
                }
            },
            label = "onboarding_step"
        ) { current ->
            when (current) {
                0 -> WelcomeStep(onNext = { viewModel.nextStep() })
                1 -> NameStep(
                    name = name,
                    onNameChange = viewModel::setName,
                    onNext = { viewModel.nextStep() }
                )
                2 -> HowItWorksStep(
                    name = name,
                    onNext = { viewModel.nextStep() }
                )
                3 -> ReminderStep(
                    name = name,
                    timeOfDay = timeOfDay,
                    notificationTime = notificationTime,
                    onTimeOfDayChange = viewModel::setTimeOfDay,
                    onTimeChange = viewModel::setNotificationTime,
                    onNext = { viewModel.nextStep() }
                )
                4 -> AllSetStep(
                    name = name,
                    onFinish = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.completeOnboarding()
                            onFinished()
                        }
                    }
                )
            }
        }
    }
}

// ── Shared UI helpers ────────────────────────────────────────────────────────

enum class CompanionExpression {
    Warmheart, Thinking, Waving, Sleeping, Gratitude;

    val assetName: String get() = name.lowercase()
}

@Composable
private fun rememberCompanionPainter(
    companion: String,
    expression: CompanionExpression
): androidx.compose.ui.graphics.painter.Painter {
    val context = LocalContext.current
    return remember(companion, expression) {
        val bitmap = context.assets
            .open("$companion/expressions/${companion}_${expression.assetName}.webp")
            .use { android.graphics.BitmapFactory.decodeStream(it) }
        androidx.compose.ui.graphics.painter.BitmapPainter(bitmap.asImageBitmap())
    }
}

@Composable
private fun CompanionImage(
    companion: String,
    expression: CompanionExpression,
    height: Dp,
    modifier: Modifier = Modifier,
    floating: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "companion_float")
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "companion_float_y"
    )
    val offsetY = if (floating) floatY else 0f

    Image(
        painter = rememberCompanionPainter(companion, expression),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .height(height)
            .offset(y = offsetY.dp)
    )
}

@Composable
private fun ProgressDots(totalSteps: Int, currentStep: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { index ->
            val isActive = index == currentStep
            Box(
                modifier = Modifier
                    .height(7.dp)
                    .width(if (isActive) 22.dp else 7.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isActive) Warm else OnboardingDot)
            )
        }
    }
}

@Composable
private fun OnboardingButton(
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
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun OnboardingScaffold(
    background: Color = OnboardingBg,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 26.dp)
            .padding(bottom = 30.dp),
        content = content
    )
}

// ── Step 0: Welcome ──────────────────────────────────────────────────────────

@Composable
private fun WelcomeStep(onNext: () -> Unit) {
    OnboardingScaffold {
        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            CompanionImage(
                companion = "ori",
                expression = CompanionExpression.Warmheart,
                height = 210.dp,
                floating = true
            )
        }
        Column {
            Text(
                text = stringResource(R.string.onboarding_welcome_title),
                style = MaterialTheme.typography.headlineLarge,
                color = OnboardingText
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.onboarding_welcome_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = OnboardingTextSecondary
            )
            ProgressDots(totalSteps = 5, currentStep = 0, modifier = Modifier.padding(top = 22.dp))
            Spacer(Modifier.height(18.dp))
            OnboardingButton(text = stringResource(R.string.onboarding_welcome_cta), onClick = onNext)
        }
    }
}

// ── Step 1: Name ─────────────────────────────────────────────────────────────

@Composable
private fun NameStep(name: String, onNameChange: (String) -> Unit, onNext: () -> Unit) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    OnboardingScaffold {
        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            CompanionImage(
                companion = "ori",
                expression = CompanionExpression.Thinking,
                height = 172.dp,
                floating = true
            )
        }
        Column {
            Text(
                text = stringResource(R.string.onboarding_name_title),
                style = MaterialTheme.typography.headlineMedium,
                color = OnboardingText
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.onboarding_name_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = OnboardingTextSecondary
            )
            Spacer(Modifier.height(18.dp))
            NameInputField(
                value = name,
                onValueChange = onNameChange,
                onDone = { if (name.isNotBlank()) onNext() },
                focusRequester = focusRequester
            )
            ProgressDots(totalSteps = 5, currentStep = 1, modifier = Modifier.padding(top = 18.dp))
            Spacer(Modifier.height(16.dp))
            OnboardingButton(
                text = stringResource(R.string.onboarding_name_cta),
                onClick = onNext,
                enabled = name.isNotBlank()
            )
        }
    }
}

@Composable
private fun NameInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
    focusRequester: FocusRequester
) {
    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it.take(40)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        textStyle = MaterialTheme.typography.headlineSmall.copy(
            color = OnboardingText,
            fontSize = 18.sp
        ),
        modifier = Modifier.focusRequester(focusRequester),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .border(2.dp, Warm, RoundedCornerShape(18.dp))
                    .padding(horizontal = 18.dp, vertical = 15.dp)
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = stringResource(R.string.onboarding_name_hint),
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 18.sp),
                        color = OnboardingTextSecondary.copy(alpha = 0.5f)
                    )
                }
                innerTextField()
            }
        }
    )
}

// ── Step 2: How it works ─────────────────────────────────────────────────────

@Composable
private fun HowItWorksStep(name: String, onNext: () -> Unit) {
    val displayName = name.ifBlank { stringResource(R.string.onboarding_name_hint) }

    OnboardingScaffold {
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            CompanionImage(
                companion = "ori",
                expression = CompanionExpression.Waving,
                height = 150.dp,
                floating = false
            )
        }
        Spacer(Modifier.height(12.dp))
        MessagePreviewCard(displayName = displayName)
        Spacer(Modifier.weight(1f))
        Column {
            Text(
                text = stringResource(R.string.onboarding_howto_title),
                style = MaterialTheme.typography.headlineSmall,
                color = OnboardingText
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.onboarding_howto_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = OnboardingTextSecondary
            )
            ProgressDots(totalSteps = 5, currentStep = 2, modifier = Modifier.padding(top = 16.dp))
            Spacer(Modifier.height(14.dp))
            OnboardingButton(text = stringResource(R.string.onboarding_howto_cta), onClick = onNext)
        }
    }
}

@Composable
private fun MessagePreviewCard(displayName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(22.dp), ambientColor = Color(0x1A3D5A3C))
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .border(1.5.dp, OnboardingCard, RoundedCornerShape(22.dp))
            .padding(18.dp)
    ) {
        Text(
            text = stringResource(R.string.onboarding_howto_preview_label),
            style = MaterialTheme.typography.labelSmall,
            color = Supportive
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = stringResource(R.string.onboarding_howto_preview_greeting, displayName),
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 15.sp),
            color = Positive
        )
        Spacer(Modifier.height(7.dp))
        Text(
            text = stringResource(R.string.onboarding_howto_preview_message),
            style = MaterialTheme.typography.titleLarge,
            color = OnboardingText
        )
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            ActionIconButton(
                icon = { Icon(Icons.Default.Favorite, null, tint = Warm, modifier = Modifier.size(17.dp)) },
                background = Color(0xFFFFF0EB)
            )
            ActionIconButton(
                icon = { Icon(Icons.Default.ThumbUp, null, tint = Positive, modifier = Modifier.size(16.dp)) },
                background = Color(0xFFEEF4EF)
            )
        }
    }
}

@Composable
private fun ActionIconButton(icon: @Composable () -> Unit, background: Color) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

// ── Step 3: Reminder ─────────────────────────────────────────────────────────

@Composable
private fun ReminderStep(
    name: String,
    timeOfDay: TimeOfDay,
    notificationTime: String,
    onTimeOfDayChange: (TimeOfDay) -> Unit,
    onTimeChange: (String) -> Unit,
    onNext: () -> Unit
) {
    val displayName = name.ifBlank { stringResource(R.string.onboarding_name_hint) }

    OnboardingScaffold(background = OnboardingBgSage) {
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            CompanionImage(
                companion = "ori",
                expression = CompanionExpression.Sleeping,
                height = 140.dp,
                floating = false
            )
        }
        Spacer(Modifier.height(16.dp))
        Column {
            Text(
                text = stringResource(R.string.onboarding_reminder_title, displayName),
                style = MaterialTheme.typography.headlineMedium,
                color = OnboardingText
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.onboarding_reminder_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = OnboardingTextSecondary
            )
            Spacer(Modifier.height(16.dp))
            TimeOfDaySelector(
                selected = timeOfDay,
                onSelect = onTimeOfDayChange
            )
            Spacer(Modifier.height(12.dp))
            TimeDisplayRow(time = notificationTime)
            ProgressDots(totalSteps = 5, currentStep = 3, modifier = Modifier.padding(top = 16.dp))
            Spacer(Modifier.height(14.dp))
            OnboardingButton(
                text = stringResource(R.string.onboarding_reminder_cta),
                onClick = onNext
            )
        }
    }
}

@Composable
private fun TimeOfDaySelector(selected: TimeOfDay, onSelect: (TimeOfDay) -> Unit) {
    val items = listOf(
        Triple(TimeOfDay.Morning, "☀️", R.string.onboarding_reminder_morning),
        Triple(TimeOfDay.Noon, "⛅", R.string.onboarding_reminder_noon),
        Triple(TimeOfDay.Evening, "🌙", R.string.onboarding_reminder_evening),
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { (tod, emoji, labelRes) ->
            val isSelected = tod == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .shadow(
                        elevation = if (isSelected) 6.dp else 0.dp,
                        shape = RoundedCornerShape(18.dp),
                        ambientColor = Warm.copy(alpha = 0.18f)
                    )
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .border(
                        2.dp,
                        if (isSelected) Warm else Color(0xFFEFE7D8),
                        RoundedCornerShape(18.dp)
                    )
                    .clickable { onSelect(tod) },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = emoji, fontSize = 18.sp)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = stringResource(labelRes),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) OnboardingText else OnboardingTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeDisplayRow(time: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.5.dp, Color(0xFFEFE7D8), RoundedCornerShape(18.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.onboarding_reminder_time_label),
            style = MaterialTheme.typography.bodyMedium,
            color = OnboardingTextSecondary
        )
        Text(
            text = time,
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp),
            color = Positive
        )
    }
}

// ── Step 4: All set ──────────────────────────────────────────────────────────

@Composable
private fun AllSetStep(name: String, onFinish: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFFFFF6E3), Color(0xFFFDF0DC), Color(0xFFF7E6CF)),
                    radius = 1200f
                )
            )
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 26.dp)
                .padding(bottom = 30.dp)
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CompanionImage(
                companion = "ori",
                    expression = CompanionExpression.Gratitude,
                    height = 215.dp,
                    floating = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column {
                Text(
                    text = stringResource(R.string.onboarding_allset_title, name.trim()),
                    style = MaterialTheme.typography.headlineLarge,
                    color = OnboardingText
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.onboarding_allset_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnboardingTextSecondary
                )
                ProgressDots(totalSteps = 5, currentStep = 4, modifier = Modifier.padding(top = 22.dp))
                Spacer(Modifier.height(18.dp))
                OnboardingButton(
                    text = stringResource(R.string.onboarding_allset_cta),
                    onClick = onFinish,
                    color = Positive
                )
            }
        }
    }
}
