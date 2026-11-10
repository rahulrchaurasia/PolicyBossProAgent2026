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

    // =========================================================================
    // 5. SHIMMER LOADING AND TAB SWITCHING TESTS
    // =========================================================================

    @Test
    fun testHomeScreen_shimmerCondition_activatesOnExplicitIsShimmerLoading() {
        // Even when cached data is present, isShimmerLoading should trigger shimmer (e.g. onHomeTabSelected)
        val stateWithCacheAndShimmer = HomeUiState(
            isLoading = false,
            isOnline = true,
            isShimmerLoading = true,
            isInitialLoadCompleted = true,
            leads = AppDummyData.topLeads
        )

        assertTrue("isShimmerLoading must be true", stateWithCacheAndShimmer.isShimmerLoading)
        assertTrue("showShimmer must evaluate to true when isShimmerLoading is true", stateWithCacheAndShimmer.showShimmer)
    }

    @Test
    fun testHomeRepository_userConstantsResultContract() = kotlinx.coroutines.runBlocking {
        var fetchCallsCount = 0
        val fakeHomeRepository = object : com.policyboss.policybosspro.core.repository.home.HomeRepository {
            override suspend fun fetchAndSaveUserConstants(): Result<com.policyboss.policybosspro.feature.login.data.model.UserConstantResponse?> {
                fetchCallsCount++
                return Result.success(
                    com.policyboss.policybosspro.feature.login.data.model.UserConstantResponse(
                        status = "SUCCESS",
                        message = "User constants loaded"
                    )
                )
            }
            override suspend fun fetchAndSaveUserConstants(body: Map<String, String>): retrofit2.Response<com.policyboss.policybosspro.feature.login.data.model.UserConstantResponse>? = null
            override fun getEarningsSummary() = kotlinx.coroutines.flow.flowOf(Result.success(AppDummyData.earningsSummary))
            override fun getQuoteTabs() = kotlinx.coroutines.flow.flowOf(Result.success(AppDummyData.quoteVehicleTabs))
            override fun getPromoBanners() = kotlinx.coroutines.flow.flowOf(Result.success(AppDummyData.promoBanners))
            override fun getHomeMetrics() = kotlinx.coroutines.flow.flowOf(Result.success(AppDummyData.homeMetrics))
            override fun getTopLeads() = kotlinx.coroutines.flow.flowOf(Result.success(AppDummyData.topLeads))
            override fun getRenewalLeads() = kotlinx.coroutines.flow.flowOf(Result.success(AppDummyData.renewalLeads))
            override fun getQuickLinks() = kotlinx.coroutines.flow.flowOf(Result.success(AppDummyData.quickLinks))
            override fun getTrustBullets() = kotlinx.coroutines.flow.flowOf(Result.success(AppDummyData.trustBullets))
            override fun getPartnerLogos() = kotlinx.coroutines.flow.flowOf(Result.success(AppDummyData.partnerLogos))
        }

        val result = fakeHomeRepository.fetchAndSaveUserConstants()
        assertTrue(result.isSuccess)
        org.junit.Assert.assertEquals("SUCCESS", result.getOrNull()?.status)
        org.junit.Assert.assertEquals(1, fetchCallsCount)
    }
}

