package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.PolicyBossTheme
import com.policyboss.policybosspro.ui.theme.bodyMediumSemiBold

/**
 * "Loading more" footer for the quote list while premium_list_db is still being polled:
 * shimmering placeholder card(s), a spinner with "Fetching more quotes… x of y insurers
 * responded" and a thin progress bar.
 *
 * @param skeletonCount 1 under existing quotes; 2 when no quote has arrived yet.
 * @param progressText e.g. "11 of 18 insurers responded"; null shows only "Fetching more quotes…".
 * @param progress answered / total in 0..1; null shows an indeterminate bar.
 * @param hasQuotes false before any plan is listed ("Fetching quotes…"), true after ("Fetching more quotes…").
 */
@Composable
fun QuoteFetchingFooter(
    progressText: String?,
    progress: Float?,
    modifier: Modifier = Modifier,
    skeletonCount: Int = 1,
    hasQuotes: Boolean = true
) {
    val statusText = quoteFetchingStatusText(hasQuotes, progressText)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = statusText },
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(skeletonCount) { QuoteCardSkeleton() }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = AppColors.PrimaryBlue,
                trackColor = AppColors.PrimaryBlue.copy(alpha = 0.14f),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyMediumSemiBold,
                color = AppColors.TextSecondary,
                textAlign = TextAlign.Center
            )
        }

        val barModifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(999.dp))
        if (progress != null) {
            val animatedProgress by animateFloatAsState(
                targetValue = progress.coerceIn(0f, 1f),
                animationSpec = tween(durationMillis = 400),
                label = "quoteFetchProgress"
            )
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = barModifier,
                color = AppColors.PrimaryBlue,
                trackColor = AppColors.Slate200,
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
        } else {
            LinearProgressIndicator(
                modifier = barModifier,
                color = AppColors.PrimaryBlue,
                trackColor = AppColors.Slate200,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

/**
 * Footer status line: "Fetching quotes…" before any plan is listed, "Fetching more quotes…" after,
 * followed by the insurer progress when known (e.g. "3 of 18 insurers responded").
 */
internal fun quoteFetchingStatusText(hasQuotes: Boolean, progressText: String?): String =
    listOfNotNull(if (hasQuotes) "Fetching more quotes…" else "Fetching quotes…", progressText).joinToString(" ")

/** Shown once polling stopped at the time limit while some insurers had not answered. */
@Composable
fun QuoteFetchTimedOutNote(modifier: Modifier = Modifier) {
    Text(
        text = "Some insurers didn't respond in time",
        style = MaterialTheme.typography.bodyMediumSemiBold,
        color = AppColors.TextSecondary,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

/** Placeholder with the outline of an insurer quote card and a moving shimmer. */
@Composable
private fun QuoteCardSkeleton(modifier: Modifier = Modifier) {
    val shimmer = rememberShimmerBrush()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.White)
            .border(1.dp, AppColors.Slate200, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SkeletonBlock(shimmer, width = 46.dp, height = 28.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SkeletonBlock(shimmer, width = 120.dp, height = 14.dp)
                SkeletonBlock(shimmer, width = 64.dp, height = 10.dp)
            }
        }
        SkeletonBlock(shimmer, width = 90.dp, height = 22.dp)
        SkeletonBlock(shimmer, height = 40.dp)
        SkeletonBlock(shimmer, height = 40.dp, cornerRadius = 999.dp)
    }
}

@Composable
private fun SkeletonBlock(
    brush: Brush,
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp,
    cornerRadius: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .then(if (width != null) Modifier.width(width) else Modifier.fillMaxWidth())
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
    )
}

@Composable
private fun rememberShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "quoteSkeletonShimmer")
    val offset by transition.animateFloat(
        initialValue = -400f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )
    return Brush.linearGradient(
        colors = listOf(AppColors.Slate100, AppColors.Slate50, AppColors.Slate100),
        start = Offset(offset, 0f),
        end = Offset(offset + 400f, 0f)
    )
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Fetching footer - under quotes", showBackground = true, widthDp = 360)
@Composable
private fun QuoteFetchingFooterPreview() {
    PolicyBossTheme {
        QuoteFetchingFooter(
            progressText = "11 of 18 insurers responded",
            progress = 11f / 18f,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Fetching footer - no quotes yet", showBackground = true, widthDp = 360)
@Composable
private fun QuoteFetchingFooterEmptyPreview() {
    PolicyBossTheme {
        QuoteFetchingFooter(
            progressText = null,
            progress = null,
            skeletonCount = 2,
            hasQuotes = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Timed out note", showBackground = true, widthDp = 360)
@Composable
private fun QuoteFetchTimedOutNotePreview() {
    PolicyBossTheme {
        QuoteFetchTimedOutNote(modifier = Modifier.padding(16.dp))
    }
}
