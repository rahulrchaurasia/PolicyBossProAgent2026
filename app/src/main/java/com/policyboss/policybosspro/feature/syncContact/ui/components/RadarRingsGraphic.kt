package com.policyboss.policybosspro.feature.syncContact.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun RadarRingsGraphic(
    modifier: Modifier = Modifier,
    ringColor: Color = AppColors.RadarRingDark,
    ringCount: Int = 4,
    minRadiusFraction: Float = 0.22f,
    maxRadiusFraction: Float = 0.85f,
    strokeWidthDp: Float = 1.2f,
    enablePulse: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarPulse")
    val pulseAlpha by if (enablePulse) {
        infiniteTransition.animateFloat(
            initialValue = 0.7f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "PulseAlpha"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(1.0f) }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val maxRadius = (size.minDimension / 2f) * maxRadiusFraction
        val minRadius = (size.minDimension / 2f) * minRadiusFraction

        val step = if (ringCount > 1) (maxRadius - minRadius) / (ringCount - 1) else 0f

        for (i in 0 until ringCount) {
            val radius = minRadius + (i * step)
            val alphaFactor = (1f - (i.toFloat() / ringCount * 0.4f)).coerceIn(0.2f, 1f)
            drawCircle(
                color = ringColor.copy(alpha = ringColor.alpha * pulseAlpha * alphaFactor),
                radius = radius,
                center = androidx.compose.ui.geometry.Offset(centerX, centerY),
                style = Stroke(width = strokeWidthDp.dp.toPx())
            )
        }
    }
}

