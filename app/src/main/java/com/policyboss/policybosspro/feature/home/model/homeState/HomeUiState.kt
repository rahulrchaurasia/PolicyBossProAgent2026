package com.policyboss.policybosspro.feature.home.model.homeState

import androidx.compose.runtime.Immutable
import com.policyboss.policybosspro.feature.home.model.bossepidia.BosspediaArticle
import com.policyboss.policybosspro.feature.home.model.bossepidia.BosspediaStory
import com.policyboss.policybosspro.feature.home.model.home.EarningsSummary
import com.policyboss.policybosspro.feature.home.model.home.HomeLead
import com.policyboss.policybosspro.feature.home.model.home.HomeMetric
import com.policyboss.policybosspro.feature.home.model.home.PromoBanner
import com.policyboss.policybosspro.feature.home.model.home.QuickLink
import com.policyboss.policybosspro.feature.home.model.home.RenewalOption
import com.policyboss.policybosspro.feature.home.model.home.TrustBulletPoint
import com.policyboss.policybosspro.feature.home.model.menu.QuoteTabItem
import com.policyboss.policybosspro.feature.home.model.policy.CuratedPolicy
import com.policyboss.policybosspro.feature.home.model.vault.VaultPolicy
import com.policyboss.policybosspro.feature.home.model.video.VideoModel
import com.policyboss.policybosspro.feature.home.util.VehicleRegistrationValidator

@Immutable
data class HomeUiState(
    // User Context
    val userName: String = "Guest User",
    val userInitials: String = "PB",
    val supportNumber: String = "9224624999",

    // Earnings
    val earningsSummary: EarningsSummary = EarningsSummary(),

    // Quotes Section
    val quoteTabs: List<QuoteTabItem> = emptyList(),
    val selectedQuoteTab: String = "Car",
    val registrationNumber: String = "",
    val registrationError: String? = null,
    val isGetQuotesEnabled: Boolean = true,

    // Promo Banners Carousel
    val promoBanners: List<PromoBanner> = emptyList(),

    // Metrics / Highlights
    val metrics: List<HomeMetric> = emptyList(),

    // Top Leads for You
    val selectedLeadsTab: String = "Sync Leads",
    val leads: List<HomeLead> = emptyList(),
    val renewalLeads: List<HomeLead> = emptyList(),

    // Quick Links & Trust
    val quickLinks: List<QuickLink> = emptyList(),
    val trustBullets: List<TrustBulletPoint> = emptyList(),
    val partnerLogos: List<Int> = emptyList(),

    // State indicators
    val isLoading: Boolean = false,
    val isShimmerLoading: Boolean = false,
    val isOnline: Boolean = true,
    val isInitialLoadCompleted: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val showNoRegistrationBottomSheet: Boolean = false,

    // Figma Bottom Sheets
    val showSyncIncentiveBottomSheet: Boolean = false,
    val selectedSyncIncentiveLead: HomeLead? = null,
    val showRenewPolicyBottomSheet: Boolean = false,
    val selectedRenewalLead: HomeLead? = null,
    val selectedRenewalOption: RenewalOption = RenewalOption.EXISTING_INSURER,

    // Compatibility support for additional features
    val curatedPolicies: List<CuratedPolicy> = emptyList(),
    val selectedVaultPolicy: VaultPolicy? = null,
    val selectedVaultTab: Int = 0,
    val vaultPolicies: List<VaultPolicy> = emptyList(),
    val bosspediaStories: List<BosspediaStory> = emptyList(),
    val bosspediaArticles: List<BosspediaArticle> = emptyList(),
    val videos: List<VideoModel> = emptyList()
) {
    /**
     * Indicates whether usable cached data is available on the client.
     */
    val hasCachedData: Boolean
        get() = leads.isNotEmpty() || promoBanners.isNotEmpty() || metrics.isNotEmpty() || quoteTabs.isNotEmpty()

    val isOffline: Boolean
        get() = !isOnline

    /**
     * Determines whether to display full-screen No Internet state:
     * When offline, no cached data is available, and an error occurred or initial load has not completed, and not actively loading.
     */
    val showNoInternetScreen: Boolean
        get() = !isOnline && !hasCachedData && (errorMessage != null || !isInitialLoadCompleted) && !isLoading

    /**
     * Determines whether the Home shimmer skeleton loader should be displayed.
     * Active when [isShimmerLoading] is explicitly set, or during initial [isLoading]
     * when cached home data has not yet arrived.
     */
    val showShimmer: Boolean
        get() = !showNoInternetScreen && (isShimmerLoading || (isLoading && !hasCachedData))

    val isShimmerVisible: Boolean
        get() = showShimmer

    val registrationPlaceholder: String
        get() = VehicleRegistrationValidator.getPlaceholder(selectedQuoteTab)

    val isRegistrationNumberValid: Boolean
        get() = VehicleRegistrationValidator.isValid(selectedQuoteTab, registrationNumber)
}
