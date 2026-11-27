package com.policyboss.policybosspro.feature.carInsurance

import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.feature.carInsurance.data.repository.PremiumQuoteBundle
import com.policyboss.policybosspro.feature.carInsurance.data.repository.PremiumQuoteBundleEvent
import com.policyboss.policybosspro.feature.carInsurance.data.repository.PremiumQuoteBundleEvent.Step
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumPollStatus
import com.policyboss.policybosspro.feature.carInsurance.model.toPremiumInitiateInput
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** premium_initiate -> premium_summary -> premium_list_db, as one bundle. */
class PremiumQuoteBundleTest {

    private val input = NewCarUiState().toPremiumInitiateInput(sampleKiaVehicle("RJ14-1234"))

    @Test
    fun `runs initiate, summary, then list in that order`() = runTest {
        val repo = FakeCarInsuranceRepository()

        val events = PremiumQuoteBundle(repo).run(input).toList()

        assertEquals(listOf("initiate", "summary", "list"), repo.quoteApiCalls)
        assertEquals(listOf("SRN-11380001"), repo.premiumSummarySearches)
        assertEquals(listOf("SRN-11380001"), repo.premiumQuoteSearches)
        assertTrue(events[0] is PremiumQuoteBundleEvent.Initiated)
        assertEquals(18, (events[1] as PremiumQuoteBundleEvent.Summary).progress.total)
        assertEquals(PremiumPollStatus.COMPLETE, (events[2] as PremiumQuoteBundleEvent.Quotes).status)
    }

    @Test
    fun `summary failure is not fatal and the list is still polled`() = runTest {
        val repo = FakeCarInsuranceRepository(premiumSummaryAnswer = { Resource.Error("summary down") })

        val events = PremiumQuoteBundle(repo).run(input).toList()

        assertEquals(listOf("initiate", "summary", "list"), repo.quoteApiCalls)
        assertTrue(events.none { it is PremiumQuoteBundleEvent.Summary || it is PremiumQuoteBundleEvent.Failed })
        assertTrue(events.last() is PremiumQuoteBundleEvent.Quotes)
    }

    @Test
    fun `initiate failure stops before summary and list`() = runTest {
        val repo = FakeCarInsuranceRepository(premium = { Resource.Error("Server busy") })

        val events = PremiumQuoteBundle(repo).run(input).toList()

        assertEquals(listOf("initiate"), repo.quoteApiCalls)
        assertEquals(listOf(PremiumQuoteBundleEvent.Failed(Step.INITIATE, "Server busy")), events)
    }

    @Test
    fun `blank search reference fails at initiate without calling summary or list`() = runTest {
        val repo = FakeCarInsuranceRepository(premium = { Resource.Success(samplePremiumInitiateResult().copy(requestUniqueId = "")) })

        val events = PremiumQuoteBundle(repo).run(input).toList()

        assertEquals(listOf("initiate"), repo.quoteApiCalls)
        assertEquals(Step.INITIATE, (events.single() as PremiumQuoteBundleEvent.Failed).step)
    }

    @Test
    fun `list failure is reported as LIST`() = runTest {
        val repo = FakeCarInsuranceRepository(premiumQuotes = { Resource.Error("list down") })

        val events = PremiumQuoteBundle(repo).run(input).toList()

        assertEquals(PremiumQuoteBundleEvent.Failed(Step.LIST, "list down"), events.last())
    }
}
