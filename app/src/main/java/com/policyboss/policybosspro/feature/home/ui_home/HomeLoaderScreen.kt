package com.policyboss.policybosspro.feature.home.ui_home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.PolicyBossTheme
import com.policyboss.policybosspro.ui.theme.gradients.AppGradients
import com.policyboss.policybosspro.utils.extension.shimmerLoadingAnimation

/**
 * Pixel-perfect Shimmer Skeleton Loader Screen for the PolicyBoss Pro Homepage.
 * Perfectly mirrors the exact layout hierarchy of HomeScreen and Figma design:
 * 1. Pinned Top Bar Header (Avatar, Wallet Pill, Support, Language, Menu)
 * 2. Background gradient (Ice Blue / Sky Blue fading to surface)
 * 3. Earnings Hero Section (Month earnings amount, subtitle, and potential banner with coin)
 * 4. Get Quotes Card (Title, Category tab pills, Registration outlined box, "Get quotes" button, link)
 * 5. Promo Banner Carousel card
 * 6. Metrics Highlights Row (Expiring & Sold cards)
 * 7. Leads Section (Tabs & Lead card item)
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

    val density = LocalDensity.current
    val headerGradientHeight = 360.dp + topSafeArea
    val headerGradient = remember(headerGradientHeight, density) {
        AppGradients.screenSurfaceGradient(endY = with(density) { headerGradientHeight.toPx() })
    }

    val listContentPadding = remember(contentPadding, topSafeArea) {
        PaddingValues(
            top = topSafeArea + 58.dp + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 80.dp,
            start = 16.dp,
            end = 16.dp
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        // 1. Edge-to-edge Top Header Background Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerGradientHeight)
                .background(headerGradient)
        )

        // 2. Scrollable Shimmer Content Skeleton
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = listContentPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            userScrollEnabled = false
        ) {
            // =====================================================
            // A. EARNINGS HERO SECTION SKELETON
            // =====================================================
            item(key = "shimmer_earnings_section") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Hero Earnings Amount & Label
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // "₹20,000" Large Text Placeholder
                        Box(
                            modifier = Modifier
                                .width(150.dp)
                                .height(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .shimmerLoadingAnimation(isLoading = true)
                        )

                        // "This month's earnings" Label Placeholder
                        Box(
                            modifier = Modifier
                                .width(135.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .shimmerLoadingAnimation(isLoading = true)
                        )
                    }

                    // Potential Earnings Banner (₹1,20,000 potential this month)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColors.PotentialCardBackground)
                            .border(BorderStroke(1.dp, AppColors.SegmentedBorder), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Text placeholder
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(76.dp)
                                        .height(14.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .shimmerLoadingAnimation(isLoading = true)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(110.dp)
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .shimmerLoadingAnimation(isLoading = true)
                                )
                            }

                            // Gold coin placeholder
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .shimmerLoadingAnimation(isLoading = true)
                            )
                        }
                    }
                }
            }

            // =====================================================
            // B. GET QUOTES CARD SKELETON
            // =====================================================
            item(key = "shimmer_get_quotes_card") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                    border = BorderStroke(1.dp, AppColors.BorderSecondary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title: "Get Quotes"
                        Box(
                            modifier = Modifier
                                .width(110.dp)
                                .height(22.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .shimmerLoadingAnimation(isLoading = true)
                        )

                        // Vehicle Category Tabs Row (Car, TW, CV)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Selected Tab (Car)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AppColors.Background)
                                    .border(BorderStroke(1.dp, AppColors.BorderSecondary), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(52.dp)
                                        .height(16.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .shimmerLoadingAnimation(isLoading = true)
                                )
                            }

                            // Tab 2 (TW)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AppColors.Background)
                                    .border(BorderStroke(1.dp, AppColors.BorderSecondary), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(46.dp)
                                        .height(16.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .shimmerLoadingAnimation(isLoading = true)
                                )
                            }

                            // Tab 3 (CV)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AppColors.Background)
                                    .border(BorderStroke(1.dp, AppColors.BorderSecondary), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(46.dp)
                                        .height(16.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .shimmerLoadingAnimation(isLoading = true)
                                )
                            }
                        }

                        // Registration Input Box Outline Skeleton
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppColors.Surface)
                                .border(BorderStroke(1.dp, AppColors.BorderSecondary), RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(180.dp)
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .shimmerLoadingAnimation(isLoading = true)
                            )
                        }

                        // Primary Action Button Skeleton ("Get quotes ->")
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(CircleShape)
                                .shimmerLoadingAnimation(isLoading = true)
                        )

                        // Link text: "I don't have a registration number >"
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(210.dp)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .shimmerLoadingAnimation(isLoading = true)
                            )
                        }
                    }
                }
            }

            // =====================================================
            // C. PROMO BANNER CAROUSEL SKELETON
            // =====================================================
            item(key = "shimmer_promo_banner") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(154.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shimmerLoadingAnimation(isLoading = true)
                )
            }

            // =====================================================
            // D. METRICS HIGHLIGHT ROW SKELETON (2 Cards Side-by-Side)
            // =====================================================
            item(key = "shimmer_metrics_row") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(106.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .shimmerLoadingAnimation(isLoading = true)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(106.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .shimmerLoadingAnimation(isLoading = true)
                    )
                }
            }

            // =====================================================
            // E. LEADS PREVIEW SKELETON (Tabs + Card)
            // =====================================================
            item(key = "shimmer_leads_preview") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Leads Header Segmented Tabs
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(32.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .shimmerLoadingAnimation(isLoading = true)
                        )
                        Box(
                            modifier = Modifier
                                .width(90.dp)
                                .height(32.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .shimmerLoadingAnimation(isLoading = true)
                        )
                    }

                    // Lead Card Skeleton Item
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .shimmerLoadingAnimation(isLoading = true)
                    )
                }
            }
        }

        // =====================================================
        // 3. FIXED PINNED TOP BAR SKELETON (Matching HomeTopBar)
        // =====================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = topSafeArea)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Profile Initials Avatar Skeleton
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .shimmerLoadingAnimation(isLoading = true)
            )

            // Right Action Controls Skeleton Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Wallet Pill Button Skeleton
                Surface(
                    shape = CircleShape,
                    color = AppColors.Surface,
                    modifier = Modifier.height(48.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(96.dp)
                            .height(48.dp)
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .shimmerLoadingAnimation(isLoading = true)
                        )
                    }
                }

                // Support Circular Button Skeleton
                Surface(
                    shape = CircleShape,
                    color = AppColors.Surface,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .shimmerLoadingAnimation(isLoading = true)
                        )
                    }
                }

                // Language Circular Button Skeleton
//                Surface(
//                    shape = CircleShape,
//                    color = AppColors.Surface,
//                    modifier = Modifier.size(48.dp)
//                ) {
//                    Box(contentAlignment = Alignment.Center) {
//                        Box(
//                            modifier = Modifier
//                                .size(24.dp)
//                                .clip(CircleShape)
//                                .shimmerLoadingAnimation(isLoading = true)
//                        )
//                    }
//                }

                // Menu Hamburger Circular Button Skeleton
                Surface(
                    shape = CircleShape,
                    color = AppColors.Surface,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .shimmerLoadingAnimation(isLoading = true)
                        )
                    }
                }
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
