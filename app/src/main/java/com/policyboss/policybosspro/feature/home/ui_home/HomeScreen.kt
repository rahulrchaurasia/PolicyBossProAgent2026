package com.policyboss.policybosspro.feature.home.ui_home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.home.component.home.card.EarningsCard
import com.policyboss.policybosspro.feature.home.component.home.card.GetQuotesCard

import com.policyboss.policybosspro.feature.home.component.home.card.MetricsHighlightsRow
import com.policyboss.policybosspro.feature.home.component.home.card.PromoBannerCarousel
import com.policyboss.policybosspro.feature.home.component.home.card.QuickLinksSection
import com.policyboss.policybosspro.feature.home.component.home.card.RelationshipManagerCard
import com.policyboss.policybosspro.feature.home.component.home.card.SyncContactsBanner
import com.policyboss.policybosspro.feature.home.component.home.card.TopLeadsHeader
import com.policyboss.policybosspro.feature.home.component.home.card.ViewAllSyncLeadsLink
import com.policyboss.policybosspro.feature.home.component.home.footer.TrustAdvisoryFooter
import com.policyboss.policybosspro.feature.home.component.home.header.HomeTopBar
import com.policyboss.policybosspro.feature.home.component.home.item.LeadCardItem
import com.policyboss.policybosspro.feature.home.model.home.HomeLead
import com.policyboss.policybosspro.feature.home.model.home.PromoBanner
import com.policyboss.policybosspro.feature.home.model.homeState.HomeAction
import com.policyboss.policybosspro.feature.home.model.homeState.HomeUiState
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.gradients.AppGradients

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    // 0a. Show full-screen No Internet state if offline with no cached data
    if (uiState.showNoInternetScreen) {
        com.policyboss.policybosspro.ui.components.offline.NoInternetContent(
            onRetry = { onAction(HomeAction.OnRetryClick) },
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding)
        )
        return
    }

    // 0b. Show full-screen shimmer skeleton loader during initial or post-login data loading
    if (uiState.showShimmer) {
        HomeLoaderScreen(
            modifier = modifier,
            contentPadding = contentPadding
        )
        return
    }

    // 1. Safe area handling for edge-to-edge status bar & camera cutout
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val displayCutoutTop = WindowInsets.displayCutout.asPaddingValues().calculateTopPadding()
    val topSafeArea = max(contentPadding.calculateTopPadding(), max(statusBarTop, displayCutoutTop))

    // 2. Remember LazyListState to preserve scroll position and recycling state
    val listState = rememberLazyListState()

    // 3. Efficient scroll state observation to animate sticky top bar background
    val isScrolled by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }

    val topBarWhiteAlpha by animateFloatAsState(
        targetValue = if (isScrolled) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "topBarWhiteAlpha"
    )

    val topBarElevation by animateDpAsState(
        targetValue = if (isScrolled) 3.dp else 0.dp,
        animationSpec = tween(durationMillis = 200),
        label = "topBarElevation"
    )

    // Seamlessly aligned gradient spanning from top of screen through header & earnings
    val density = LocalDensity.current
    val headerGradientHeight = 360.dp + topSafeArea
    val headerGradient = remember(headerGradientHeight, density) {
        AppGradients.screenSurfaceGradient(endY = with(density) { headerGradientHeight.toPx() })
    }

    // 4. Content starts below pinned HomeTopBar (58dp) + topSafeArea + 12dp spacing
    val listContentPadding = remember(contentPadding, topSafeArea) {
        PaddingValues(
            top = topSafeArea + 58.dp + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 80.dp,
            start = 16.dp,
            end = 16.dp
        )
    }

    // 4. Remember derived leads list
    val currentLeads = remember(uiState.selectedLeadsTab, uiState.leads, uiState.renewalLeads) {
        if (uiState.selectedLeadsTab == "Sync Leads") {
            uiState.leads
        } else {
            uiState.renewalLeads
        }
    }

    // 5. Stable remembered callbacks so child composables are skipped during scroll
    val onProfileClick = remember(onAction) { { onAction(HomeAction.OnProfileClick) } }
    val onWalletClick = remember(onAction) { { onAction(HomeAction.OnWalletClick) } }
    val onSupportClick = remember(onAction) { { onAction(HomeAction.OnSupportClick) } }
    val onNotificationClick = remember(onAction) { { onAction(HomeAction.OnNotificationClick) } }
    val onMenuClick = remember(onAction) { { onAction(HomeAction.OnMenuClick) } }

    val onQuoteTabSelected = remember(onAction) { { tab: String -> onAction(HomeAction.OnQuoteTabSelected(tab)) } }
    val onRegistrationChange = remember(onAction) { { reg: String -> onAction(HomeAction.OnRegistrationNumberChange(reg)) } }
    val onGetQuoteClick = remember(onAction, uiState.registrationNumber, uiState.selectedQuoteTab) {
        { onAction(HomeAction.OnGetQuotesClick(uiState.registrationNumber, uiState.selectedQuoteTab)) }
    }
    val onNoRegistrationClick = remember(onAction) { { onAction(HomeAction.OnNoRegistrationClick) } }

    val onBannerClick = remember(onAction) { { banner: PromoBanner -> onAction(HomeAction.OnPromoBannerClick(banner.id)) } }
    val onSellPolicyClick = remember(onAction) { { bannerId: String -> onAction(HomeAction.OnSellPolicyClick(bannerId)) } }

    val onMetricClick = remember(onAction) { { metricId: String -> onAction(HomeAction.OnMetricClick(metricId)) } }

    val onLeadsTabSelected = remember(onAction) { { tab: String -> onAction(HomeAction.OnLeadsTabSelected(tab)) } }
    val onLeadClick = remember(onAction) { { lead: HomeLead -> onAction(HomeAction.OnLeadClick(lead)) } }
    val onSyncRewardClick = remember(onAction) { { lead: HomeLead -> onAction(HomeAction.OnOpenSyncIncentiveBottomSheet(lead)) } }
    val onShareLeadClick = remember(onAction) { { lead: HomeLead -> onAction(HomeAction.OnShareLeadClick(lead)) } }
    val onViewAllQuotesClick = remember(onAction) { { lead: HomeLead -> onAction(HomeAction.OnViewAllQuotesClick(lead)) } }
    val onViewAllSyncLeadsClick = remember(onAction) { { onAction(HomeAction.OnViewAllSyncLeadsClick) } }
    val onViewAllRenewalsClick = remember(onAction) { { onAction(HomeAction.OnViewAllRenewalsClick) } }

    val onSyncContactsClick = remember(onAction) { { onAction(HomeAction.OnSyncContactsClick) } }
    val onQuickLinkClick = remember(onAction) { { linkId: String -> onAction(HomeAction.OnQuickLinkClick(linkId)) } }
    val onConnectRMClick = remember(onAction) { { onAction(HomeAction.OnConnectRMClick) } }
    val onPartnerLogoClick = remember(onAction) { { logoRes: Int -> onAction(HomeAction.OnPartnerLogoClick(logoRes)) } }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        // Top header background gradient extending edge-to-edge behind status bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerGradientHeight)
                .background(headerGradient)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = listContentPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Non-blocking offline indicator when cached data is available without internet
            if (!uiState.isOnline && uiState.hasCachedData) {
                item(key = "home_offline_banner") {
                    com.policyboss.policybosspro.ui.components.offline.OfflineBanner(
                        message = "You're offline. Displaying saved data.",
                        onRetry = { onAction(HomeAction.OnRetryClick) }
                    )
                }
            }
            // Error banner with retry if initial data fetch failed
            if (uiState.errorMessage != null && uiState.leads.isEmpty()) {
                item(key = "home_error") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppColors.AlertCardBackground),
                        border = BorderStroke(1.dp, AppColors.AlertCardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Unable to load Home data",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.ErrorRed
                            )
                            Text(
                                text = uiState.errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = AppColors.TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { onAction(HomeAction.OnRetryClick) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryBlue)
                            ) {
                                Text(text = "Retry", color = Color.White)
                            }
                        }
                    }
                }
            }

            // 1. Earnings Section Card
            item(key = "earnings_card", contentType = "earnings_card") {
                EarningsCard(
                    earningsSummary = uiState.earningsSummary
                )
            }

            // 3. Get Quotes Card
            item(key = "get_quotes_card", contentType = "get_quotes_card") {
                GetQuotesCard(
                    tabs = uiState.quoteTabs,
                    selectedTab = uiState.selectedQuoteTab,
                    onTabSelected = onQuoteTabSelected,
                    registrationNumber = uiState.registrationNumber,
                    onRegistrationChange = onRegistrationChange,
                    onGetQuoteClick = onGetQuoteClick,
                    onNoRegistrationClick = onNoRegistrationClick
                )
            }

            // 4. Promo Banners Carousel
            item(key = "promo_banner_carousel", contentType = "promo_banner_carousel") {
                PromoBannerCarousel(
                    banners = uiState.promoBanners,
                    onBannerClick = onBannerClick,
                    onSellPolicyClick = onSellPolicyClick
                )
            }

            // 5. Metrics Highlight Row (Expiring & Sold Policy cards)
            item(key = "metrics_highlights", contentType = "metrics_highlights") {
                MetricsHighlightsRow(
                    metrics = uiState.metrics,
                    onMetricClick = onMetricClick
                )
            }

            // 6. Top Leads Header
            item(key = "leads_header", contentType = "leads_header") {
                TopLeadsHeader(
                    selectedTab = uiState.selectedLeadsTab,
                    onTabSelected = onLeadsTabSelected
                )
            }

            // 7. Top Leads for You - Individual items for smooth recycling and zero-jank frame rates
            if (currentLeads.isEmpty() && !uiState.isLoading) {
                item(key = "empty_leads_card", contentType = "empty_leads_card") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                        border = BorderStroke(1.dp, AppColors.BorderSecondary)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "No ${uiState.selectedLeadsTab} Available",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextPrimary
                            )
                            Text(
                                text = "Check back later or tap below to refresh.",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppColors.TextSecondary
                            )
                            OutlinedButton(
                                onClick = { onAction(HomeAction.OnRetryClick) },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, AppColors.BorderPrimary)
                            ) {
                                Text(text = "Refresh", color = AppColors.PrimaryBlue)
                            }
                        }
                    }
                }
            } else {
                items(
                    items = currentLeads,
                    key = { "lead_${it.id}" },
                    contentType = { "lead_card" }
                ) { lead ->
                    LeadCardItem(
                        lead = lead,
                        onLeadClick = { onLeadClick(lead) },
                        onSyncRewardClick = { onSyncRewardClick(lead) },
                        onShareClick = { onShareLeadClick(lead) },
                        onViewQuotesClick = { onViewAllQuotesClick(lead) }
                    )
                }
            }

            // 8. View All Sync Leads / Renewals Link
            item(key = "leads_view_all", contentType = "leads_view_all") {
                val isRenewals = uiState.selectedLeadsTab == "Renewals"
                ViewAllSyncLeadsLink(
                    title = if (isRenewals) "View all Renewals" else "View all Sync Leads",
                    onClick = if (isRenewals) onViewAllRenewalsClick else onViewAllSyncLeadsClick
                )
            }

            // 9. Boss Alerts Contact Sync Promotional Banner
            item(key = "sync_contacts_banner", contentType = "sync_contacts_banner") {
                SyncContactsBanner(
                    onSyncClick = onSyncContactsClick
                )
            }

            // 10. Quick Links Grid
            item(key = "quick_links_section", contentType = "quick_links_section") {
                QuickLinksSection(
                    quickLinks = uiState.quickLinks,
                    onLinkClick = onQuickLinkClick
                )
            }

            // 11. Relationship Manager Support Card
            item(key = "relationship_manager_card", contentType = "relationship_manager_card") {
                RelationshipManagerCard(
                    onConnectClick = onConnectRMClick
                )
            }

            // 11. Comparison + Convenience + Advisory Footer
            item(key = "trust_advisory_footer", contentType = "trust_advisory_footer") {
                TrustAdvisoryFooter(
                    bullets = uiState.trustBullets,
                    partnerLogos = uiState.partnerLogos,
                    onPartnerLogoClick = onPartnerLogoClick
                )
            }
        }

        // 3. Sticky / Pinned Top Bar Header (ScreenSurfaceGradient by default, solid white when pinned)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            color = Color.Transparent,
            shadowElevation = topBarElevation
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerGradient)
            ) {
                // Solid white overlay that fades in on scroll so content never shows behind it
                if (topBarWhiteAlpha > 0f) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.White.copy(alpha = topBarWhiteAlpha))
                    )
                }

                // Top Bar content sitting safely below camera punch hole & status bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = topSafeArea) // Respects status bar & camera punch hole!
                        .padding(horizontal = 16.dp)
                ) {
                    HomeTopBar(
                        userInitials = uiState.userInitials,
                        onProfileClick = onProfileClick,
                        onWalletClick = onWalletClick,
                        onSupportClick = onSupportClick,
                        onMenuClick = onMenuClick,
                        modifier  = modifier
                    )
                }

                // Subtle bottom border when pinned to give a clean separation from white cards
                if (topBarWhiteAlpha > 0f) {
                    HorizontalDivider(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        thickness = 0.5.dp,
                        color = AppColors.ClaimDivider.copy(alpha = topBarWhiteAlpha)
                    )
                }
            }
        }
    }
}

/**
 * Backward-compatible overload for existing navigation call sites.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    registrationNumber: String,
    onRegistrationChange: (String) -> Unit,
    selectedQuoteTab: String,
    onTabSelected: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onGetQuoteClick: () -> Unit,
    onNavigateToMenu: () -> Unit
) {
    val state = remember(registrationNumber, selectedQuoteTab) {
        HomeUiState(
            registrationNumber = registrationNumber,
            selectedQuoteTab = selectedQuoteTab
        )
    }

    val onActionCallback: (HomeAction) -> Unit = remember(
        onNavigateToProfile,
        onNavigateToMenu,
        onRegistrationChange,
        onTabSelected,
        onGetQuoteClick
    ) {
        { action ->
            when (action) {
                is HomeAction.OnProfileClick -> onNavigateToProfile()
                is HomeAction.OnMenuClick -> onNavigateToMenu()
                is HomeAction.OnRegistrationNumberChange -> onRegistrationChange(action.regNo)
                is HomeAction.OnQuoteTabSelected -> onTabSelected(action.tabTitle)
                is HomeAction.OnGetQuotesClick -> onGetQuoteClick()
                else -> Unit
            }
        }
    }

    HomeScreen(
        uiState = state,
        onAction = onActionCallback,
        modifier = modifier,
        contentPadding = contentPadding
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F6F8, name = "Figma Home Screen Preview")
@Composable
fun HomeScreenFigmaPreview() {
    MaterialTheme {
        HomeScreen(
            uiState = HomeUiState(
                userInitials = "DD",
                earningsSummary = AppDummyData.earningsSummary,
                quoteTabs = AppDummyData.quoteVehicleTabs,
                selectedQuoteTab = "Car",
                registrationNumber = "MH04AB1234",
                promoBanners = AppDummyData.promoBanners,
                metrics = AppDummyData.homeMetrics,
                leads = AppDummyData.topLeads
            ),
            onAction = {},
            contentPadding = PaddingValues(0.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F6F8, name = "Home Screen - Shimmer Loading")
@Composable
fun HomeScreenShimmerPreview() {
    MaterialTheme {
        HomeScreen(
            uiState = HomeUiState(
                isShimmerLoading = true
            ),
            onAction = {},
            contentPadding = PaddingValues(0.dp)
        )
    }
}