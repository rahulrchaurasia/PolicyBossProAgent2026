package com.policyboss.policybosspro.feature.grow.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.grow.model.GrowContestItem
import com.policyboss.policybosspro.ui.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun GrowContestSection(
    contests: List<GrowContestItem>,
    onContestClick: (GrowContestItem) -> Unit,
    modifier: Modifier = Modifier,
    autoScroll: Boolean = true,
    autoScrollDurationMs: Long = 4000L
) {
    if (contests.isEmpty()) return

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { contests.size }
    )
    val coroutineScope = rememberCoroutineScope()
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    // Auto-advance banner every `autoScrollDurationMs` when more than 1 contest is available
    if (autoScroll && contests.size > 1) {
        LaunchedEffect(contests.size, autoScrollDurationMs) {
            while (isActive) {
                delay(autoScrollDurationMs)
                // Don't auto-advance if user is actively dragging or pager is mid-scroll
                if (!isDragged && !pagerState.isScrollInProgress) {
                    val nextPage = (pagerState.currentPage + 1) % contests.size
                    pagerState.animateScrollToPage(
                        page = nextPage,
                        animationSpec = tween(durationMillis = 600)
                    )
                }
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Ongoing Contests",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.Slate900
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Pager for swiping between contest banners
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            key = { page -> "${contests[page].id}_$page" },
            pageSpacing = 12.dp,
            beyondViewportPageCount = 1
        ) { page ->
            val contest = contests[page]
            Image(
                painter = painterResource(id = contest.bannerRes),
                contentDescription = contest.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2.35f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onContestClick(contest) }
            )
        }

        // Animated dot indicators matching the current page
        if (contests.size > 1) {
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                contests.forEachIndexed { index, _ ->
                    val isSelected = pagerState.currentPage == index
                    val dotWidth by animateDpAsState(
                        targetValue = if (isSelected) 16.dp else 6.dp,
                        animationSpec = tween(durationMillis = 300),
                        label = "contest_dot_width"
                    )
                    val dotColor by animateColorAsState(
                        targetValue = if (isSelected) AppColors.DotActive else AppColors.DotInactive,
                        animationSpec = tween(durationMillis = 300),
                        label = "contest_dot_color"
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(6.dp)
                            .width(dotWidth)
                            .clip(CircleShape)
                            .background(dotColor)
                            .clickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun GrowContestSectionPreview() {
    GrowContestSection(
        contests = AppDummyData.growContests,
        onContestClick = {}
    )
}
