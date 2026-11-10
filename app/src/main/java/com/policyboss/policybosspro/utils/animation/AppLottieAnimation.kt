package com.policyboss.policybosspro.utils.animation

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.RenderMode

@Composable
fun AppLottieAnimation(
    @RawRes rawResId: Int,
    modifier: Modifier = Modifier,
    iterations: Int = 1,
    speed: Float = 1.0f,
    isPlaying: Boolean = true,
    restartOnPlay: Boolean = true,
    renderMode: RenderMode = RenderMode.AUTOMATIC,
    contentScale: ContentScale = ContentScale.Fit,
    ignoreSystemAnimatorScale: Boolean = false,
    onAnimationEnd: () -> Unit = {}
) = com.policyboss.policybosspro.ui.components.animation.AppLottieAnimation(
    rawResId = rawResId,
    modifier = modifier,
    iterations = iterations,
    speed = speed,
    isPlaying = isPlaying,
    restartOnPlay = restartOnPlay,
    renderMode = renderMode,
    contentScale = contentScale,
    ignoreSystemAnimatorScale = ignoreSystemAnimatorScale,
    onAnimationEnd = onAnimationEnd
)
