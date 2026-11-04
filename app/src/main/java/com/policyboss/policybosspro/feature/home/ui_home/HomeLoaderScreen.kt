package com.policyboss.policybosspro.feature.home.ui_home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.PolicyBossTheme
import com.policyboss.policybosspro.utils.extension.shimmerLoadingAnimation

/**
 * Shimmer skeleton loader screen for the Home destination.
 * Matches the visual hierarchy of the Home screen (Header, Quick Actions, Quotes, and Cards)
 * while initial or post-login APIs are being fetched.
 */
@Composable
fun HomeLoaderScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    // Edge-to-edge safe area handling for status bar & display cutout
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val displayCutoutTop = WindowInsets.displayCutout.asPaddingValues().calculateTopPadding()
    val topSafeArea = max(contentPadding.calculateTopPadding(), max(statusBarTop, displayCutoutTop))

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.Background),
        contentPadding = PaddingValues(
            top = topSafeArea + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 32.dp,
            start = 16.dp,
            end = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // =====================================================
        // 1. HERO / HEADER SECTION SKELETON (Top Bar + Banner)
        // =====================================================
        item(key = "shimmer_header") {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header Row: Welcome Text + Profile Initials Circle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        // "Welcome back!" skeleton line
                        Box(
                            modifier = Modifier
                                .width(180.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .shimmerLoadingAnimation(isLoading = true)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Subtitle / User details skeleton line
                        Box(
                            modifier = Modifier
                                .width(240.dp)
                                .height(14.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .shimmerLoadingAnimation(isLoading = true)
                        )
                    }

                    // Profile Initials Circle Skeleton
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .shimmerLoadingAnimation(isLoading = true)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Promo Banner / Earnings Hero Card Skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .shimmerLoadingAnimation(isLoading = true)
                )
            }
        }

        // =====================================================
        // 2. GET QUOTES / ACTION CARD SKELETON
        // =====================================================
        item(key = "shimmer_quotes_card") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .shimmerLoadingAnimation(isLoading = true)
            )
        }

        // =====================================================
        // 3. QUICK ACTIONS GRID SKELETON (2x2 Layout)
        // =====================================================
        item(key = "shimmer_quick_actions") {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(150.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .shimmerLoadingAnimation(isLoading = true)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(150.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .shimmerLoadingAnimation(isLoading = true)
                    )
                }

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(150.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .shimmerLoadingAnimation(isLoading = true)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(150.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .shimmerLoadingAnimation(isLoading = true)
                    )
                }
            }
        }

        // =====================================================
        // 4. EARNING OPPORTUNITY / METRICS SECTION SKELETON
        // =====================================================
        item(key = "shimmer_earning_opportunity") {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Section Title Skeleton
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(22.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .shimmerLoadingAnimation(isLoading = true)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Card Skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .shimmerLoadingAnimation(isLoading = true)
                )
            }
        }

        // =====================================================
        // 5. LEADS CARDS SKELETON
        // =====================================================
        item(key = "shimmer_leads_preview") {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tab Row Skeleton
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(110.dp)
                            .height(34.dp)
                            .clip(RoundedCornerShape(17.dp))
                            .shimmerLoadingAnimation(isLoading = true)
                    )
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(34.dp)
                            .clip(RoundedCornerShape(17.dp))
                            .shimmerLoadingAnimation(isLoading = true)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Lead Card Skeleton Item
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shimmerLoadingAnimation(isLoading = true)
                )
            }
        }
    }
}

// ==========================================
// PREVIEWS
// ==========================================

@Preview(
    name = "Home Loader Screen Preview",
    showBackground = true,
    backgroundColor = 0xFFF4F6F8
)
@Composable
private fun HomeLoaderScreenPreview() {
    PolicyBossTheme {
        HomeLoaderScreen(
            contentPadding = PaddingValues(0.dp)
        )
    }
}

