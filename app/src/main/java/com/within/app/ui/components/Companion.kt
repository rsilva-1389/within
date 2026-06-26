package com.within.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Ori (and future companions) expressions, loaded from assets/<companion>/expressions/.
 * Shared so onboarding and the journey render the mascot identically.
 */
enum class CompanionExpression {
    Warmheart, Thinking, Waving, Sleeping, Gratitude;

    val assetName: String get() = name.lowercase()
}

@Composable
fun rememberCompanionPainter(companion: String, expression: CompanionExpression): Painter {
    val context = LocalContext.current
    return remember(companion, expression) {
        val bitmap = context.assets
            .open("$companion/expressions/${companion}_${expression.assetName}.webp")
            .use { android.graphics.BitmapFactory.decodeStream(it) }
        BitmapPainter(bitmap.asImageBitmap())
    }
}

@Composable
fun CompanionImage(
    expression: CompanionExpression,
    height: Dp,
    modifier: Modifier = Modifier,
    companion: String = "ori",
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
