package com.policyboss.policybosspro.ui.components.animation

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.RenderMode
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

/**
 * Reusable, production-grade Lottie Animation Composable for Jetpack Compose.
 *
 * Key Architectural Optimizations:
 * 1. Synchronized Playback: Animates if and only if `composition != null` (`isPlaying = isPlaying && composition != null`)
 *    preventing single-shot animations (`iterations = 1`) from prematurely evaluating terminal frame 0 before loading finishes.
 * 2. Frame-0 Reset Guarantee: Exposes and defaults `restartOnPlay = true` to force animation progress reset to frame 0
 *    whenever the composable enters the screen or switches state.
 * 3. Defer Recomposition: Uses lambda overload `progress = { progress }` on LottieAnimation to avoid recomposing
 *    the parent layout tree at 60/120 Hz.
 * 4. Lifecycle-Safe Completion: Uses LaunchedEffect to safely invoke `onAnimationEnd` outside the composition phase.
 * 5. Rendering Engine Fallback: Exposes `renderMode` with `RenderMode.AUTOMATIC` default and hardware/software options.
 */
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
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(rawResId)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying && composition != null,
        iterations = iterations,
        speed = speed,
        restartOnPlay = restartOnPlay,
        ignoreSystemAnimatorScale = ignoreSystemAnimatorScale
    )

    // Safely fire completion callback outside composition phase
    LaunchedEffect(progress) {
        if (progress >= 1.0f && iterations == 1) {
            onAnimationEnd()
        }
    }

    LottieAnimation(
        composition = composition,
        progress = { progress },
        renderMode = renderMode,
        contentScale = contentScale,
        modifier = modifier
    )
}
