package com.policyboss.policybosspro.feature.home.model.homeState

import com.policyboss.policybosspro.feature.home.model.home.HomeLead
import com.policyboss.policybosspro.feature.home.model.vault.VaultPolicy

/*
  Unidirectional Data Flow (UDF):
  UiState (StateFlow): Represents what the UI is.
  HomeAction: What the user does (Clicks, text inputs, tab changes). Flow flows UP from UI to ViewModel.
  HomeUiEvent (Channel/SharedFlow): One-time actions (Navigation, Snackbar, Dialer). Flows DOWN from ViewModel to UI.
*/

sealed interface HomeAction {
    // Lifecycle & Refresh Actions
    data object OnRetryClick : HomeAction
    data object OnRefresh : HomeAction

    // Header Actions
    data object OnProfileClick : HomeAction
    data object OnWalletClick : HomeAction
    data object OnSupportClick : HomeAction
    data object OnNotificationClick : HomeAction
    data object OnMenuClick : HomeAction

    // Quote Section Actions
    data class OnQuoteTabSelected(val tabTitle: String) : HomeAction
    data class OnRegistrationNumberChange(val regNo: String) : HomeAction
    data class OnGetQuotesClick(val regNo: String, val tab: String) : HomeAction
    data object OnNoRegistrationClick : HomeAction
    data object OnDismissNoRegistrationBottomSheet : HomeAction

    // Banner Actions
    data class OnPromoBannerClick(val bannerId: String) : HomeAction
    data class OnSellPolicyClick(val bannerId: String) : HomeAction
    data class OnEarningBannerClick(val bannerId: String) : HomeAction
    data object OnPrivilegeBannerClick : HomeAction

    // Metric Highlights Actions
    data class OnMetricClick(val metricId: String) : HomeAction

    // Leads & Quotes Actions
    data class OnLeadsTabSelected(val tab: String) : HomeAction
    data class OnLeadClick(val lead: HomeLead) : HomeAction
    data class OnShareLeadClick(val lead: HomeLead) : HomeAction
    data class OnViewAllQuotesClick(val lead: HomeLead) : HomeAction
    data object OnViewAllSyncLeadsClick : HomeAction
    data object OnViewAllRenewalsClick : HomeAction

    // Sync Incentive Popup Actions
    data class OnOpenSyncIncentiveBottomSheet(val lead: HomeLead) : HomeAction
    data object OnDismissSyncIncentiveBottomSheet : HomeAction
    data class OnUnlockSyncRewardClick(val lead: HomeLead) : HomeAction

    // Renew Policy Popup Actions
    data class OnOpenRenewPolicyBottomSheet(val lead: HomeLead) : HomeAction
    data object OnDismissRenewPolicyBottomSheet : HomeAction
    data class OnSelectRenewalOption(val option: com.policyboss.policybosspro.feature.home.model.home.RenewalOption) : HomeAction
    data class OnContinueRenewalClick(
        val lead: HomeLead,
        val option: com.policyboss.policybosspro.feature.home.model.home.RenewalOption
    ) : HomeAction

    // Contact Sync Promo Banner
    data object OnSyncContactsClick : HomeAction

    // Quick Links & Support
    data class OnQuickLinkClick(val linkId: String) : HomeAction
    data object OnConnectRMClick : HomeAction

    // Partner Insurers
    data class OnPartnerLogoClick(val logoRes: Int) : HomeAction

    // Navigation & Legacy Support
    data object OnViewAllVaultClick : HomeAction
    data object OnExploreBosspediaClick : HomeAction
    data object OnAssistanceClick : HomeAction
    data class OnQuickActionClick(val actionId: String) : HomeAction
    data class OnPolicyCategoryClick(val category: String) : HomeAction

    // BottomSheet Actions
    data class OnShowPolicyBottomSheetClick(val policy: VaultPolicy) : HomeAction
    data object OnDismissPolicyBottomSheet : HomeAction

    // Vault & Video Actions
    data class OnVaultTabSelected(val index: Int) : HomeAction
    data object OnVideoViewMoreClick : HomeAction
    data class OnVideoClick(val videoId: String) : HomeAction
}