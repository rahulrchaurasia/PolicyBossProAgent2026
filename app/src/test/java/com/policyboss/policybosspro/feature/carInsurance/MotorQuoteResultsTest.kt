package com.policyboss.policybosspro.feature.carInsurance

import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.feature.carInsurance.data.api.CarInsuranceApi
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteResponse
import com.policyboss.policybosspro.feature.carInsurance.data.repository.CarInsuranceRepository
import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuote
import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuoteDefaults
import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuoteSortOption
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiEvent
import com.policyboss.policybosspro.feature.carInsurance.viewmodel.NewCarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class MotorQuoteResultsTest {

    private val testDispatcher = StandardTestDispatcher()


    private fun fakeRepository(quotes: List<MotorQuote> = MotorQuoteDefaults.generateDefaultQuotes()) =
        object : FakeCarInsuranceRepository() {
            override suspend fun getCarQuotes(request: CarQuoteRequest): Resource<CarQuoteResponse> =
                Resource.Success(
                    CarQuoteResponse(
                        status = "SUCCESS",
                        quoteId = "Q-TEST-001",
                        quoteUrl = "https://www.policyboss.com/test",
                        quotesCount = quotes.size
                    )
                )
        }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `ensureQuotesLoaded initializes default quotes when state is empty`() {
        val vm = NewCarViewModel(fakeRepository())
        assertTrue(vm.uiState.value.quotes.isEmpty())

        vm.ensureQuotesLoaded()

        assertFalse(vm.uiState.value.quotes.isEmpty())
        assertEquals(8, vm.uiState.value.quotes.size)
        assertEquals("ACKO-01", vm.uiState.value.quotes.first().id)
        assertTrue(vm.uiState.value.expandedAddOnQuoteIds.contains("ACKO-01"))
    }

    @Test
    fun `ToggleZextraBundle toggles discount and affects effective pricing`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.ensureQuotesLoaded()

        // Initially Zextra is ON
        assertTrue(vm.uiState.value.isZextraBundleEnabled)
        val quote = vm.uiState.value.quotes.first()
        assertEquals(6700, quote.effectivePremium(isZextraEnabled = true))
        assertEquals(1403, quote.effectiveEarnings(isZextraEnabled = true))

        // Toggle OFF
        vm.onAction(NewCarAction.ToggleZextraBundle)
        assertFalse(vm.uiState.value.isZextraBundleEnabled)
        assertEquals(7300, quote.effectivePremium(isZextraEnabled = false))
        assertEquals(1303, quote.effectiveEarnings(isZextraEnabled = false))

        // Toggle back ON
        vm.onAction(NewCarAction.ToggleZextraBundle)
        assertTrue(vm.uiState.value.isZextraBundleEnabled)
    }

    @Test
    fun `ToggleAddOnsExpanded expands and collapses quote add-ons section`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.ensureQuotesLoaded()

        assertTrue(vm.uiState.value.expandedAddOnQuoteIds.contains("ACKO-01"))

        // Collapse
        vm.onAction(NewCarAction.ToggleAddOnsExpanded("ACKO-01"))
        assertFalse(vm.uiState.value.expandedAddOnQuoteIds.contains("ACKO-01"))

        // Expand again
        vm.onAction(NewCarAction.ToggleAddOnsExpanded("ACKO-01"))
        assertTrue(vm.uiState.value.expandedAddOnQuoteIds.contains("ACKO-01"))
    }

    @Test
    fun `ToggleCompareQuote limits maximum comparison to 3 quotes`() = runTest {
        val vm = NewCarViewModel(fakeRepository())
        vm.ensureQuotesLoaded()

        val events = mutableListOf<NewCarUiEvent>()
        val job = launch { vm.uiEvent.collect { events.add(it) } }

        vm.onAction(NewCarAction.ToggleCompareQuote("ACKO-01"))
        vm.onAction(NewCarAction.ToggleCompareQuote("ACKO-02"))
        vm.onAction(NewCarAction.ToggleCompareQuote("TATA-01"))

        assertEquals(3, vm.uiState.value.selectedQuotesForComparison.size)

        // Attempting to add 4th must trigger snackbar and reject addition
        vm.onAction(NewCarAction.ToggleCompareQuote("HDFC-01"))
        advanceUntilIdle()

        assertEquals(3, vm.uiState.value.selectedQuotesForComparison.size)
        assertTrue(events.any { it is NewCarUiEvent.ShowSnackbar && it.message.contains("up to 3") })

        // Removing one works
        vm.onAction(NewCarAction.ToggleCompareQuote("ACKO-01"))
        assertEquals(2, vm.uiState.value.selectedQuotesForComparison.size)

        job.cancel()
    }

    @Test
    fun `sorting by lowest premium and highest earnings orders quotes correctly`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.ensureQuotesLoaded()

        // Sort by Lowest Premium
        vm.onAction(NewCarAction.OnSortOptionSelected(MotorQuoteSortOption.LOWEST_PREMIUM))
        val lowestFirst = vm.uiState.value.sortedQuotes
        for (i in 0 until lowestFirst.size - 1) {
            assertTrue(
                lowestFirst[i].effectivePremium(true) <= lowestFirst[i + 1].effectivePremium(true)
            )
        }

        // Sort by Highest Earnings
        vm.onAction(NewCarAction.OnSortOptionSelected(MotorQuoteSortOption.HIGHEST_EARNINGS))
        val highestFirst = vm.uiState.value.sortedQuotes
        for (i in 0 until highestFirst.size - 1) {
            assertTrue(
                highestFirst[i].effectiveEarnings(true) >= highestFirst[i + 1].effectiveEarnings(true)
            )
        }
    }

    @Test
    fun `Indian currency formatter formats thousands, lakhs, and small amounts correctly`() {
        assertEquals("6,403", MotorQuote.formatIndianCurrency(6403))
        assertEquals("7,33,600", MotorQuote.formatIndianCurrency(733600))
        assertEquals("675", MotorQuote.formatIndianCurrency(675))
        assertEquals("50", MotorQuote.formatIndianCurrency(50))
    }

    @Test
    fun `Buy Now, Policy Details, and Share actions emit appropriate UI events`() = runTest {
        val vm = NewCarViewModel(fakeRepository())
        vm.ensureQuotesLoaded()

        val events = mutableListOf<NewCarUiEvent>()
        val job = launch { vm.uiEvent.collect { events.add(it) } }

        val quote = vm.uiState.value.quotes.first()

        // Buy Now
        vm.onAction(NewCarAction.OnBuyNowClick(quote))
        advanceUntilIdle()
        assertTrue(events.any { it is NewCarUiEvent.OpenUrl && it.url == quote.buyNowUrl })

        // Policy Details
        vm.onAction(NewCarAction.OnPolicyDetailsClick(quote))
        advanceUntilIdle()
        assertTrue(events.contains(NewCarUiEvent.NavigateToPolicyDetail))
        assertEquals(quote, vm.uiState.value.selectedPolicyDetailQuote)

        // Share Quote
        vm.onAction(NewCarAction.OnShareQuoteClick(quote))
        advanceUntilIdle()
        assertTrue(events.any { it is NewCarUiEvent.ShareText && it.text.contains(quote.insurerName) })

        // Edit Vehicle Details
        vm.onAction(NewCarAction.OnEditVehicleClick)
        advanceUntilIdle()
        assertTrue(events.contains(NewCarUiEvent.NavigateBackToForm))

        job.cancel()
    }
}

