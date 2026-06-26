package com.within.app.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
    val mood by viewModel.mood.collectAsState()
    val desiredFeeling by viewModel.desiredFeeling.collectAsState()
    val timeOfDay by viewModel.timeOfDay.collectAsState()
    val notificationTime by viewModel.notificationTime.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        viewModel.completeOnboarding(onDone = onFinished)
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
                OnboardingSteps.COVER -> CoverStep(onNext = viewModel::nextStep)
                OnboardingSteps.INTRO -> IntroStep(onNext = viewModel::nextStep)
                OnboardingSteps.NAME -> NameStep(
                    name = name,
                    onNameChange = viewModel::setName,
                    onNext = viewModel::nextStep
                )
                OnboardingSteps.MOOD -> MoodStep(
                    name = name,
                    selected = mood,
                    onSelect = viewModel::setMood,
                    onNext = viewModel::nextStep
                )
                OnboardingSteps.RESPOND -> RespondStep(
                    name = name,
                    mood = mood,
                    onNext = viewModel::nextStep
                )
                OnboardingSteps.FEELING -> FeelingStep(
                    selected = desiredFeeling,
                    onSelect = viewModel::setDesiredFeeling,
                    onNext = viewModel::nextStep
                )
                OnboardingSteps.STAGES -> StagesStep(
                    desiredFeeling = desiredFeeling,
                    onNext = viewModel::nextStep
                )
                OnboardingSteps.DAY -> DayStep(onNext = viewModel::nextStep)
                OnboardingSteps.PROMISES -> PromisesStep(onNext = viewModel::nextStep)
                OnboardingSteps.REMINDER -> ReminderStep(
                    name = name,
                    timeOfDay = timeOfDay,
                    notificationTime = notificationTime,
                    onTimeOfDayChange = viewModel::setTimeOfDay,
                    onTimeChange = viewModel::setNotificationTime,
                    onNext = viewModel::nextStep
                )
                OnboardingSteps.ALL_SET -> AllSetStep(
                    name = name,
                    onFinish = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.completeOnboarding(onDone = onFinished)
                        }
                    }
                )
            }
        }
    }
}

// ── Shared UI helpers ────────────────────────────────────────────────────────

enum class CompanionExpression {
    Warmheart, Thinking, Waving, Sleeping, Gratitude, Compassion;

    val assetName: String get() = name.lowercase()
}

@Composable
private fun rememberCompanionPainter(
    companion: String,
    expression: CompanionExpression
): androidx.compose.ui.graphics.painter.Painter {
    val context = LocalContext.current
    return remember(companion, expression) {
        fun decode(name: String) = context.assets
            .open("$companion/expressions/${companion}_$name.webp")
            .use { android.graphics.BitmapFactory.decodeStream(it) }
        // Fall back to Warmheart if an expression asset hasn't been added yet.
        val bitmap = runCatching { decode(expression.assetName) }
            .getOrElse { decode(CompanionExpression.Warmheart.assetName) }
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

/** Image-on-top, text-and-button-at-bottom scaffold shared by Ori's narrative beats. */
@Composable
private fun OriNarrativeStep(
    expression: CompanionExpression,
    imageHeight: Dp,
    ctaText: String,
    onNext: () -> Unit,
    background: Color = OnboardingBg,
    floating: Boolean = true,
    ctaColor: Color = Warm,
    text: @Composable ColumnScope.() -> Unit
) {
    OnboardingScaffold(background = background) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            CompanionImage(
                companion = "ori",
                expression = expression,
                height = imageHeight,
                floating = floating
            )
        }
        Column {
            text()
            Spacer(Modifier.height(18.dp))
            OnboardingButton(text = ctaText, onClick = onNext, color = ctaColor)
        }
    }
}

// ── Step 0: Cover ────────────────────────────────────────────────────────────

@Composable
private fun CoverStep(onNext: () -> Unit) {
    OriNarrativeStep(
        expression = CompanionExpression.Warmheart,
        imageHeight = 210.dp,
        ctaText = stringResource(R.string.onboarding_cover_cta),
        onNext = onNext
    ) {
        Text(
            text = stringResource(R.string.onboarding_cover_title),
            style = MaterialTheme.typography.headlineLarge,
            color = OnboardingText
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.onboarding_cover_subtitle),
            style = MaterialTheme.typography.titleMedium,
            color = Supportive
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.onboarding_cover_tagline),
            style = MaterialTheme.typography.bodyLarge,
            color = OnboardingTextSecondary
        )
    }
}

// ── Step 1: Ori arrives ──────────────────────────────────────────────────────

@Composable
private fun IntroStep(onNext: () -> Unit) {
    OriNarrativeStep(
        expression = CompanionExpression.Waving,
        imageHeight = 190.dp,
        ctaText = stringResource(R.string.onboarding_intro_cta),
        onNext = onNext
    ) {
        Text(
            text = stringResource(R.string.onboarding_intro_title),
            style = MaterialTheme.typography.headlineMedium,
            color = OnboardingText
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.onboarding_intro_body),
            style = MaterialTheme.typography.bodyLarge,
            color = OnboardingTextSecondary
        )
    }
}

// ── Step 2: Name ─────────────────────────────────────────────────────────────

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
            Spacer(Modifier.height(18.dp))
            NameInputField(
                value = name,
                onValueChange = onNameChange,
                onDone = { if (name.isNotBlank()) onNext() },
                focusRequester = focusRequester
            )
            Spacer(Modifier.height(18.dp))
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

// ── Step 3: Mood check-in ────────────────────────────────────────────────────

@Composable
private fun MoodStep(
    name: String,
    selected: Mood?,
    onSelect: (Mood) -> Unit,
    onNext: () -> Unit
) {
    OnboardingScaffold {
        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            CompanionImage(
                companion = "ori",
                expression = CompanionExpression.Warmheart,
                height = 150.dp,
                floating = true
            )
        }
        Column {
            Text(
                text = stringResource(R.string.onboarding_mood_title, name.trim()),
                style = MaterialTheme.typography.headlineMedium,
                color = OnboardingText
            )
            Spacer(Modifier.height(18.dp))
            MoodGrid(selected = selected, onSelect = onSelect)
            Spacer(Modifier.height(18.dp))
            OnboardingButton(
                text = stringResource(R.string.onboarding_name_cta),
                onClick = onNext,
                enabled = selected != null
            )
        }
    }
}

@Composable
private fun MoodGrid(selected: Mood?, onSelect: (Mood) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Mood.entries.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { mood ->
                    MoodChip(
                        label = stringResource(mood.labelRes),
                        isSelected = mood == selected,
                        onClick = { onSelect(mood) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MoodChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .shadow(
                elevation = if (isSelected) 6.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Warm.copy(alpha = 0.18f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(
                2.dp,
                if (isSelected) Warm else Color(0xFFEFE7D8),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) OnboardingText else OnboardingTextSecondary
        )
    }
}

// ── Step 4: Ori responds (struggling only) ───────────────────────────────────

@Composable
private fun RespondStep(name: String, mood: Mood?, onNext: () -> Unit) {
    val responseRes = mood?.responseRes ?: R.string.onboarding_respond_tired
    OriNarrativeStep(
        expression = CompanionExpression.Compassion,
        imageHeight = 190.dp,
        ctaText = stringResource(R.string.onboarding_respond_cta),
        onNext = onNext,
        background = OnboardingBgSage,
        floating = false
    ) {
        Text(
            text = stringResource(responseRes, name.trim()),
            style = MaterialTheme.typography.headlineSmall.copy(lineHeight = 30.sp),
            color = OnboardingText
        )
    }
}

// ── Step 5: Desired feeling ──────────────────────────────────────────────────

@Composable
private fun FeelingStep(
    selected: DesiredFeeling?,
    onSelect: (DesiredFeeling) -> Unit,
    onNext: () -> Unit
) {
    OnboardingScaffold {
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            CompanionImage(
                companion = "ori",
                expression = CompanionExpression.Thinking,
                height = 140.dp,
                floating = true
            )
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = stringResource(R.string.onboarding_feeling_title),
            style = MaterialTheme.typography.headlineMedium,
            color = OnboardingText
        )
        Spacer(Modifier.height(18.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            DesiredFeeling.entries.forEach { feeling ->
                FeelingOption(
                    label = stringResource(feeling.labelRes),
                    isSelected = feeling == selected,
                    onClick = { onSelect(feeling) }
                )
            }
        }
        Spacer(Modifier.weight(1f))
        OnboardingButton(
            text = stringResource(R.string.onboarding_name_cta),
            onClick = onNext,
            enabled = selected != null
        )
    }
}

@Composable
private fun FeelingOption(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isSelected) 6.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Warm.copy(alpha = 0.18f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(
                2.dp,
                if (isSelected) Warm else Color(0xFFEFE7D8),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) OnboardingText else OnboardingTextSecondary
        )
    }
}

// ── Step 6: Ori names the journey ────────────────────────────────────────────

@Composable
private fun StagesStep(desiredFeeling: DesiredFeeling?, onNext: () -> Unit) {
    val connectorRes = desiredFeeling?.connectorRes ?: R.string.onboarding_stage_connector_peace
    OriNarrativeStep(
        expression = CompanionExpression.Waving,
        imageHeight = 150.dp,
        ctaText = stringResource(R.string.onboarding_stages_cta),
        onNext = onNext
    ) {
        Text(
            text = stringResource(connectorRes),
            style = MaterialTheme.typography.titleLarge.copy(lineHeight = 28.sp),
            color = OnboardingText
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.onboarding_stages_body),
            style = MaterialTheme.typography.bodyMedium,
            color = OnboardingTextSecondary
        )
    }
}

// ── Step 7: How a day works ──────────────────────────────────────────────────

@Composable
private fun DayStep(onNext: () -> Unit) {
    val rows = listOf(
        "🌅" to R.string.onboarding_day_reflection,
        "✍️" to R.string.onboarding_day_prompt,
        "🔍" to R.string.onboarding_day_deepdive,
        "🫧" to R.string.onboarding_day_reaffirm,
        "🚶" to R.string.onboarding_day_action,
        "🌙" to R.string.onboarding_day_evening,
    )
    OnboardingScaffold {
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.onboarding_day_title),
            style = MaterialTheme.typography.headlineSmall,
            color = OnboardingText
        )
        Spacer(Modifier.height(18.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            rows.forEach { (emoji, textRes) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = emoji, fontSize = 20.sp)
                    Spacer(Modifier.width(14.dp))
                    Text(
                        text = stringResource(textRes),
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnboardingText
                    )
                }
            }
        }
        Spacer(Modifier.weight(1f))
        OnboardingButton(text = stringResource(R.string.onboarding_day_cta), onClick = onNext)
    }
}

// ── Step 8: The promises ─────────────────────────────────────────────────────

@Composable
private fun PromisesStep(onNext: () -> Unit) {
    val promises = listOf(
        R.string.onboarding_promises_1,
        R.string.onboarding_promises_2,
        R.string.onboarding_promises_3,
    )
    OnboardingScaffold {
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            CompanionImage(
                companion = "ori",
                expression = CompanionExpression.Warmheart,
                height = 140.dp,
                floating = true
            )
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = stringResource(R.string.onboarding_promises_title),
            style = MaterialTheme.typography.headlineSmall,
            color = OnboardingText
        )
        Spacer(Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            promises.forEach { textRes ->
                Text(
                    text = stringResource(textRes),
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 26.sp),
                    color = OnboardingTextSecondary
                )
            }
        }
        Spacer(Modifier.weight(1f))
        OnboardingButton(text = stringResource(R.string.onboarding_promises_cta), onClick = onNext)
    }
}

// ── Step 9: Reminder ─────────────────────────────────────────────────────────

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
            Spacer(Modifier.height(18.dp))
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

// ── Step 10: All set ─────────────────────────────────────────────────────────

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
                Spacer(Modifier.height(22.dp))
                OnboardingButton(
                    text = stringResource(R.string.onboarding_allset_cta),
                    onClick = onFinish,
                    color = Positive
                )
            }
        }
    }
}
