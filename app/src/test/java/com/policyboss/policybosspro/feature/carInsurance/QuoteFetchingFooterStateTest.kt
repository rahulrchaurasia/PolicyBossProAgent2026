package com.policyboss.policybosspro.feature.carInsurance

import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumPollStatus
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumProgress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** What drives the results-list footer: loader while polling, note after a timeout. */
class QuoteFetchingFooterStateTest {

    @Test
    fun `footer shows while polling with answered over total progress`() {
        val state = NewCarUiState(
            premiumPollStatus = PremiumPollStatus.POLLING,
            premiumProgress = PremiumProgress(status = "pending", total = 18, pending = 7)
        )
        assertTrue(state.isFetchingMoreQuotes)
        assertEquals(11f / 18f, state.premiumProgressFraction!!, 0.0001f)
        assertEquals("11 of 18 insurers responded", state.premiumProgressText)
        assertFalse(state.isQuotesFetchTimedOut)
    }

    @Test
    fun `no progress before the total is known`() {
        val state = NewCarUiState(premiumPollStatus = PremiumPollStatus.POLLING, premiumProgress = PremiumProgress())
        assertTrue(state.isFetchingMoreQuotes)
        assertNull(state.premiumProgressFraction)
        assertNull(state.premiumProgressText)
    }

    @Test
    fun `complete hides the footer, timeout shows the note`() {
        val complete = NewCarUiState(premiumPollStatus = PremiumPollStatus.COMPLETE)
        assertFalse(complete.isFetchingMoreQuotes)
        assertFalse(complete.isQuotesFetchTimedOut)

        val timedOut = NewCarUiState(premiumPollStatus = PremiumPollStatus.TIMED_OUT)
        assertFalse(timedOut.isFetchingMoreQuotes)
        assertTrue(timedOut.isQuotesFetchTimedOut)
    }

    @Test
    fun `no empty-state message while quotes are still arriving`() {
        val fetching = NewCarUiState(
            premiumInitiate = samplePremiumInitiateResult(),
            premiumPollStatus = PremiumPollStatus.POLLING
        )
        assertFalse(fetching.isAwaitingInsurerQuotes)

        val done = fetching.copy(premiumPollStatus = PremiumPollStatus.COMPLETE)
        assertTrue(done.isAwaitingInsurerQuotes)
    }
}
