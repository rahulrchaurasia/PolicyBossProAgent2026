package com.policyboss.policybosspro.feature.home

import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.home.model.homeState.HomeUiState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeOfflineHandlingTest {

    // =========================================================================
    // 1. CASE A: NO INTERNET + NO CACHED DATA -> FULL-SCREEN NO INTERNET
    // =========================================================================

    @Test
    fun testCaseA_noInternetNoCache_showsNoInternetScreen() {
        val state = HomeUiState(
            isLoading = false,
            isOnline = false,
            isInitialLoadCompleted = false,
            errorMessage = "No internet connection",
            leads = emptyList(),
            promoBanners = emptyList(),
            metrics = emptyList()
        )

        assertFalse(state.isOnline)
        assertTrue(state.isOffline)
        assertFalse(state.hasCachedData)
        assertTrue(state.showNoInternetScreen)
        assertFalse(state.showShimmer)
    }

    // =========================================================================
    // 2. CASE B: NO INTERNET + CACHED DATA AVAILABLE -> PRESERVE CACHE
    // =========================================================================

    @Test
    fun testCaseB_noInternetWithCache_preservesContent() {
        val state = HomeUiState(
            isLoading = false,
            isOnline = false,
            isInitialLoadCompleted = true,
            leads = AppDummyData.topLeads,
            promoBanners = AppDummyData.promoBanners,
            metrics = AppDummyData.homeMetrics
        )

        assertFalse(state.isOnline)
        assertTrue(state.isOffline)
        assertTrue(state.hasCachedData)
        assertFalse(state.showNoInternetScreen)
        assertFalse(state.showShimmer)
    }

    // =========================================================================
    // 3. CASE C: INTERNET AVAILABLE + INITIAL LOADING -> SHIMMER SKELETON
    // =========================================================================

    @Test
    fun testCaseC_internetAvailableInitialLoading_showsShimmer() {
        val state = HomeUiState(
            isLoading = true,
            isOnline = true,
            isInitialLoadCompleted = false,
            leads = emptyList()
        )

        assertTrue(state.isOnline)
        assertFalse(state.isOffline)
        assertFalse(state.hasCachedData)
        assertFalse(state.showNoInternetScreen)
        assertTrue(state.showShimmer)
    }

    // =========================================================================
    // 4. AUTOMATIC RECOVERY: TRANSITION FROM OFFLINE TO ONLINE
    // =========================================================================

    @Test
    fun testAutomaticRecovery_stateTransitionToOnlineSuccess() {
        val offlineFailedState = HomeUiState(
            isLoading = false,
            isOnline = false,
            isInitialLoadCompleted = false,
            errorMessage = "No internet connection"
        )
        assertTrue(offlineFailedState.showNoInternetScreen)

        // After connectivity is restored and data loads
        val recoveredState = offlineFailedState.copy(
            isOnline = true,
            isLoading = false,
            isInitialLoadCompleted = true,
            errorMessage = null,
            leads = AppDummyData.topLeads
        )

        assertTrue(recoveredState.isOnline)
        assertTrue(recoveredState.hasCachedData)
        assertFalse(recoveredState.showNoInternetScreen)
    }
}

