package com.policyboss.policybosspro.feature.lead

import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.lead.model.LeadAction
import com.policyboss.policybosspro.feature.lead.model.LeadUiEvent
import com.policyboss.policybosspro.feature.lead.model.LeadUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LeadOfflineHandlingTest {

    // =========================================================================
    // 1. CASE A: NO INTERNET + NO CACHED LEADS -> FULL-SCREEN NO INTERNET
    // =========================================================================

    @Test
    fun testCaseA_noInternetNoCache_showsNoInternetScreen() {
        val state = LeadUiState(
            isLoading = false,
            isOnline = false,
            isInitialLoadCompleted = false,
            error = "No internet connection",
            allLeads = emptyList()
        )

        assertFalse(state.isOnline)
        assertTrue(state.isOffline)
        assertFalse(state.hasCachedData)
        assertTrue(state.showNoInternetScreen)
    }

    // =========================================================================
    // 2. CASE B: NO INTERNET + CACHED LEADS AVAILABLE -> PRESERVE LIST
    // =========================================================================

    @Test
    fun testCaseB_noInternetWithCache_preservesContent() {
        val state = LeadUiState(
            isLoading = false,
            isOnline = false,
            isInitialLoadCompleted = true,
            allLeads = AppDummyData.topLeads
        )

        assertFalse(state.isOnline)
        assertTrue(state.isOffline)
        assertTrue(state.hasCachedData)
        assertFalse(state.showNoInternetScreen)
        assertEquals(AppDummyData.topLeads.size, state.filteredLeads.size)
    }

    // =========================================================================
    // 3. CASE C: ONLINE INITIAL LOADING
    // =========================================================================

    @Test
    fun testCaseC_onlineLoading() {
        val state = LeadUiState(
            isLoading = true,
            isOnline = true,
            allLeads = emptyList()
        )

        assertTrue(state.isOnline)
        assertFalse(state.showNoInternetScreen)
    }

    // =========================================================================
    // 4. ACTION & EVENT CONTRACT TESTS
    // =========================================================================

    @Test
    fun testLeadActionsAndEvents_contracts() {
        val retryAction = LeadAction.OnRetryClick
        val snackbarEvent = LeadUiEvent.ShowSnackbar("Connection restored")

        assertEquals(LeadAction.OnRetryClick, retryAction)
        assertEquals("Connection restored", snackbarEvent.message)
    }
}

