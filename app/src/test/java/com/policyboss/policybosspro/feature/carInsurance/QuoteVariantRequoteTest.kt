package com.policyboss.policybosspro.feature.carInsurance

import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteResponse
import com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode
import com.policyboss.policybosspro.feature.carInsurance.model.CarVariantItem
import com.policyboss.policybosspro.feature.carInsurance.model.FuelVariantCatalog
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiEvent
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumInitiateInput
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumInitiateResult
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumPollStatus
import com.policyboss.policybosspro.feature.carInsurance.model.QuoteFetchState
import com.policyboss.policybosspro.feature.carInsurance.model.QuoteLoadingUiEvent
import com.policyboss.policybosspro.feature.carInsurance.viewmodel.NewCarViewModel
import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption
import kotlinx.coroutines.CompletableDeferred
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Results-screen "Select Variant" sheet: beta_GetFuelVariant list, and re-quote on change. */
@OptIn(ExperimentalCoroutinesApi::class)
class QuoteVariantRequoteTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(testDispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    /** Catalogue for the sample Kia (model SELTOS, DIESEL) with real numeric Vehicle_IDs. */
    private val seltosCatalog = FuelVariantCatalog(
        modelId = "SELTOS",
        fuels = listOf("PETROL", "DIESEL"),
        variants = listOf(
            CarVariantItem("6917", "HTK PLUS 1.5 MT", "DIESEL", modelId = "SELTOS", fuelId = "2", cubicCapacity = "1493", seatingCapacity = "5"),
            CarVariantItem("6918", "GTX PLUS 1.5 AT", "DIESEL", modelId = "SELTOS", fuelId = "2", cubicCapacity = "1493"),
            CarVariantItem("7001", "HTE 1.5 MT", "PETROL", modelId = "SELTOS", fuelId = "1", cubicCapacity = "1497")
        )
    )

    private fun repository(
        premium: suspend (PremiumInitiateInput) -> Resource<PremiumInitiateResult> = {
            Resource.Success(samplePremiumInitiateResult())
        }
    ) = object : FakeCarInsuranceRepository(
        fuelVariants = { _, _ -> Resource.Success(seltosCatalog) },
        premium = premium
    ) {
        override suspend fun getCarQuotes(request: CarQuoteRequest): Resource<CarQuoteResponse> =
            error("car-number journey must not call getCarQuotes")
    }

    /** Vahan lookup + Get Quotes + results opened. */
    private suspend fun kotlinx.coroutines.test.TestScope.openResults(vm: NewCarViewModel) {
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        advanceUntilIdle()
        vm.onAction(NewCarAction.OnGetQuotesClick)
        advanceUntilIdle()
        vm.onAction(NewCarAction.OnQuoteNavigationHandled)
    }

    @Test
    fun `sheet lists the fuel-variant catalogue for the committed fuel with the vahan variant selected`() = runTest {
        val vm = NewCarViewModel(repository())
        openResults(vm)

        vm.onAction(NewCarAction.OnShowVariantSheet(true))
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state.showVariantSheet)
        // vahan variant (not in the catalogue) first, then the DIESEL catalogue variants only
        assertEquals(
            listOf("SELTOS_PLUS_1.5_GTX_AT", "6917", "6918"),
            state.quoteVariantOptions.map { it.value }
        )
        assertEquals("HTK PLUS 1.5 MT (1493CC)", state.quoteVariantOptions[1].label)
        assertEquals("SELTOS_PLUS_1.5_GTX_AT", state.selectedQuoteVariantOption?.value)
    }

    @Test
    fun `picking another variant commits it and re-quotes in place without navigating`() = runTest {
        val repo = repository()
        val vm = NewCarViewModel(repo)
        val loadingEvents = mutableListOf<QuoteLoadingUiEvent>()
        val loadingJob = launch { vm.quoteLoadingEvent.collect { loadingEvents.add(it) } }
        openResults(vm)
        assertEquals(1, loadingEvents.count { it is QuoteLoadingUiEvent.NavigateToQuotes })

        vm.onAction(NewCarAction.OnShowVariantSheet(true))
        advanceUntilIdle()
        vm.onAction(NewCarAction.OnVariantSelected(DropdownOption("6917", "HTK PLUS 1.5 MT (1493CC)")))

        // Immediately: list cleared, results-screen loaders on, sheet closed
        val refreshing = vm.uiState.value
        assertFalse(refreshing.showVariantSheet)
        assertEquals("HTK PLUS 1.5 MT", refreshing.variant)
        assertEquals("6917", refreshing.variantId)
        assertEquals("5", refreshing.seatingCapacity)
        assertTrue(refreshing.quotes.isEmpty())
        assertTrue(refreshing.isQuoteSummaryLoading)

        advanceUntilIdle()

        // premium_initiate ran again with the new Vehicle_ID and the new quotes are bound
        assertEquals(2, repo.premiumInitiateInputs.size)
        assertEquals("6917", repo.premiumInitiateInputs.last().vehicle.variantId)
        assertEquals(2, repo.premiumQuoteSearches.size)
        val done = vm.uiState.value
        assertEquals(listOf("ARN-IFFCO", "ARN-BAJAJ"), done.quotes.map { it.id })
        assertTrue(done.quoteState is QuoteFetchState.Success)
        // Still on the results screen: no second navigation
        assertEquals(1, loadingEvents.count { it is QuoteLoadingUiEvent.NavigateToQuotes })
        loadingJob.cancel()
    }

    @Test
    fun `variant pill shows the full name with capacity, first time and after a pick`() = runTest {
        // vehicle_info_user: Variant_Name + Cubic_Capacity
        val first = com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState(
            variant = "1.0 VXI AMT BS-VI",
            cubicCapacity = "998"
        )
        assertEquals("1.0 VXI AMT BS-VI (998CC)", first.quoteVariantDisplayText)
        // Name that already carries the capacity is not doubled
        assertEquals(
            "Plus 1.5 GTX AT (1493CC)",
            first.copy(variant = "Plus 1.5 GTX AT (1493CC)", cubicCapacity = "1493").quoteVariantDisplayText
        )
        assertEquals("VXI+", first.copy(variant = "VXI+", cubicCapacity = "").quoteVariantDisplayText)
        assertEquals("", first.copy(variant = "").quoteVariantDisplayText)

        // After a pick from the sheet: same label as the sheet row
        val vm = NewCarViewModel(repository())
        openResults(vm)
        vm.onAction(NewCarAction.OnVariantSelected(DropdownOption("6917", "HTK PLUS 1.5 MT (1493CC)")))
        advanceUntilIdle()
        assertEquals("HTK PLUS 1.5 MT (1493CC)", vm.uiState.value.quoteVariantDisplayText)
        assertEquals(vm.uiState.value.selectedQuoteVariantOption?.label, vm.uiState.value.quoteVariantDisplayText)
    }

    @Test
    fun `picking the same variant just closes the sheet`() = runTest {
        val repo = repository()
        val vm = NewCarViewModel(repo)
        openResults(vm)

        vm.onAction(NewCarAction.OnShowVariantSheet(true))
        advanceUntilIdle()
        vm.onAction(NewCarAction.OnVariantSelected(vm.uiState.value.selectedQuoteVariantOption!!))
        advanceUntilIdle()

        assertFalse(vm.uiState.value.showVariantSheet)
        assertEquals(1, repo.premiumInitiateInputs.size)
        assertEquals(2, vm.uiState.value.quotes.size)
    }

    @Test
    fun `premium_initiate failure on re-quote stops the loaders and shows a message`() = runTest {
        var calls = 0
        val repo = repository(premium = {
            calls++
            if (calls == 1) Resource.Success(samplePremiumInitiateResult()) else Resource.Error("Server busy")
        })
        val vm = NewCarViewModel(repo)
        openResults(vm)
        val formEvents = mutableListOf<NewCarUiEvent>()
        val formJob = launch { vm.uiEvent.collect { formEvents.add(it) } }

        vm.onAction(NewCarAction.OnVariantSelected(DropdownOption("6918", "GTX PLUS 1.5 AT (1493CC)")))
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(PremiumPollStatus.STOPPED_ON_ERROR, state.premiumPollStatus)
        assertFalse(state.isQuoteSummaryLoading)
        assertFalse(state.isFetchingMoreQuotes)
        assertTrue(formEvents.contains(NewCarUiEvent.ShowSnackbar("Server busy")))
        formJob.cancel()
    }

    @Test
    fun `leaving results during a re-quote cancels it and Get Quotes still works`() = runTest {
        val hold = CompletableDeferred<Unit>()
        var calls = 0
        val repo = repository(premium = {
            calls++
            if (calls == 2) hold.await()
            Resource.Success(samplePremiumInitiateResult())
        })
        val vm = NewCarViewModel(repo)
        openResults(vm)

        vm.onAction(NewCarAction.OnVariantSelected(DropdownOption("6918", "GTX PLUS 1.5 AT (1493CC)")))
        advanceUntilIdle()
        vm.onAction(NewCarAction.OnLeaveQuoteResults)
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isFetchingMoreQuotes)
        assertFalse(vm.uiState.value.quoteState is QuoteFetchState.Loading)

        vm.onAction(NewCarAction.OnGetQuotesClick)
        advanceUntilIdle()
        assertEquals(3, repo.premiumInitiateInputs.size)
    }
}
