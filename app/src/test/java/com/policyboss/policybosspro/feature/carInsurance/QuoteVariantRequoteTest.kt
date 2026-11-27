package com.policyboss.policybosspro.feature.carInsurance

import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteResponse
import com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode
import com.policyboss.policybosspro.feature.carInsurance.model.CarVariantItem
import com.policyboss.policybosspro.feature.carInsurance.model.FuelVariantCatalog
import com.policyboss.policybosspro.feature.carInsurance.model.FuelVariantState
import com.policyboss.policybosspro.feature.carInsurance.model.QuotePopup
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.buildPremiumInitiateRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumInitiateRequest
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import java.time.LocalDate
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

    /** Fake whose beta_GetFuelVariant answers come from [answers] in order; the last repeats. */
    private fun variantRepository(vararg answers: Resource<FuelVariantCatalog>): FakeCarInsuranceRepository {
        var call = 0
        return object : FakeCarInsuranceRepository(
            fuelVariants = { _, _ -> answers[(call++).coerceAtMost(answers.lastIndex)] }
        ) {
            override suspend fun getCarQuotes(request: CarQuoteRequest): Resource<CarQuoteResponse> =
                error("car-number journey must not call getCarQuotes")
        }
    }

    @Test
    fun `Get Quotes refetches variants when the post-vahan prefetch failed`() = runTest {
        val repo = variantRepository(Resource.Error("Network down"), Resource.Success(seltosCatalog))
        val vm = NewCarViewModel(repo)

        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        advanceUntilIdle()
        assertEquals(1, repo.fuelVariantCalls.size)
        assertTrue(vm.uiState.value.quoteVariantLoadState is FuelVariantState.Error)

        vm.onAction(NewCarAction.OnGetQuotesClick)
        advanceUntilIdle()

        assertEquals(2, repo.fuelVariantCalls.size)
        assertEquals(FuelVariantState.Success, vm.uiState.value.quoteVariantLoadState)
        assertEquals(listOf("SELTOS_PLUS_1.5_GTX_AT", "6917", "6918"), vm.uiState.value.quoteVariantOptions.map { it.value })
    }

    @Test
    fun `Get Quotes does not refetch variants already loaded after vahan`() = runTest {
        val repo = variantRepository(Resource.Success(seltosCatalog))
        val vm = NewCarViewModel(repo)

        openResults(vm)

        assertEquals(1, repo.fuelVariantCalls.size)
        assertEquals(FuelVariantState.Success, vm.uiState.value.quoteVariantLoadState)
    }

    @Test
    fun `variant sheet keeps the committed variant on error and retries for the committed model`() = runTest {
        val repo = variantRepository(Resource.Error("Network down"))
        val vm = NewCarViewModel(repo)
        openResults(vm)

        val failed = vm.uiState.value
        assertEquals(FuelVariantState.Error("Network down"), failed.quoteVariantLoadState)
        assertEquals(listOf("SELTOS_PLUS_1.5_GTX_AT"), failed.quoteVariantOptions.map { it.value })

        val callsBefore = repo.fuelVariantCalls.size
        vm.onAction(NewCarAction.OnRetryQuoteVariants)
        advanceUntilIdle()

        assertEquals(callsBefore + 1, repo.fuelVariantCalls.size)
        assertEquals(failed.modelId, repo.fuelVariantCalls.last().first)
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
        advanceUntilIdle()

        // Pick only: sheet closed, Re-calculate popup up, nothing committed or fetched yet
        val pending = vm.uiState.value
        assertFalse(pending.showVariantSheet)
        assertEquals(QuotePopup.RecalculateVariant, pending.activeQuotePopup)
        assertEquals("6917", pending.pendingQuoteVariant?.variantId)
        assertEquals("SELTOS_PLUS_1.5_GTX_AT", pending.variantId)
        assertEquals(1, repo.premiumInitiateInputs.size)
        assertEquals(2, pending.quotes.size)

        vm.onAction(NewCarAction.OnConfirmQuotePopup)

        // Immediately: list cleared, results-screen loaders on, popup closed
        val refreshing = vm.uiState.value
        assertEquals(null, refreshing.activeQuotePopup)
        assertEquals(null, refreshing.pendingQuoteVariant)
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
        vm.onAction(NewCarAction.OnConfirmQuotePopup)
        advanceUntilIdle()
        assertEquals("HTK PLUS 1.5 MT (1493CC)", vm.uiState.value.quoteVariantDisplayText)
        assertEquals(vm.uiState.value.selectedQuoteVariantOption?.label, vm.uiState.value.quoteVariantDisplayText)
    }

    @Test
    fun `dismissing the Re-calculate popup keeps the old variant and quotes without any API call`() = runTest {
        val repo = repository()
        val vm = NewCarViewModel(repo)
        openResults(vm)
        val before = vm.uiState.value
        val apiCallsBefore = repo.quoteApiCalls.toList()

        vm.onAction(NewCarAction.OnVariantSelected(DropdownOption("6917", "HTK PLUS 1.5 MT (1493CC)")))
        advanceUntilIdle()
        assertEquals(QuotePopup.RecalculateVariant, vm.uiState.value.activeQuotePopup)

        vm.onAction(NewCarAction.OnDismissQuotePopup)
        advanceUntilIdle()

        val after = vm.uiState.value
        assertEquals(null, after.activeQuotePopup)
        assertEquals(null, after.pendingQuoteVariant)
        assertEquals(before.variantId, after.variantId)
        assertEquals(before.variant, after.variant)
        assertEquals(before.quotes, after.quotes)
        assertEquals(apiCallsBefore, repo.quoteApiCalls)
    }

    @Test
    fun `confirming runs the bundle once in order initiate, summary, list`() = runTest {
        val repo = repository()
        val vm = NewCarViewModel(repo)
        openResults(vm)
        assertEquals(listOf("initiate", "summary", "list"), repo.quoteApiCalls)

        vm.onAction(NewCarAction.OnVariantSelected(DropdownOption("6917", "HTK PLUS 1.5 MT (1493CC)")))
        vm.onAction(NewCarAction.OnConfirmQuotePopup)
        advanceUntilIdle()

        assertEquals(listOf("initiate", "summary", "list", "initiate", "summary", "list"), repo.quoteApiCalls)
        assertEquals(listOf("SRN-11380001", "SRN-11380001"), repo.premiumSummarySearches)
    }

    @Test
    fun `premium_initiate request sends crn 0 on the first quote and on a re-quote`() = runTest {
        val repo = repository()
        val vm = NewCarViewModel(repo)
        openResults(vm)
        // The first response returned a CRN; it must not leak into the next request
        assertEquals("11380001", vm.uiState.value.crnNumber)

        vm.onAction(NewCarAction.OnVariantSelected(DropdownOption("6917", "HTK PLUS 1.5 MT (1493CC)")))
        vm.onAction(NewCarAction.OnConfirmQuotePopup)
        advanceUntilIdle()

        assertEquals(2, repo.premiumInitiateInputs.size)
        repo.premiumInitiateInputs.forEach { input ->
            val request = buildPremiumInitiateRequest(input, ssId = "150253", appVersion = "test", today = LocalDate.of(2026, 11, 27))
            val json = testJson.encodeToJsonElement(PremiumInitiateRequest.serializer(), request).jsonObject
            assertEquals(JsonPrimitive(0), json["crn"])
        }
    }

    /** Repository whose premium_list_db answer carries an IDV range of 3 to 5 lakh. */
    private fun idvRepository() = object : FakeCarInsuranceRepository(
        fuelVariants = { _, _ -> Resource.Success(seltosCatalog) },
        premiumQuotes = { srn -> Resource.Success(samplePremiumQuoteList(srn).copy(minIdv = 300_000, maxIdv = 500_000)) }
    ) {
        override suspend fun getCarQuotes(request: CarQuoteRequest): Resource<CarQuoteResponse> =
            error("car-number journey must not call getCarQuotes")
    }

    @Test
    fun `applying a new IDV re-quotes in place through the bundle with that IDV`() = runTest {
        val repo = idvRepository()
        val vm = NewCarViewModel(repo)
        val loadingEvents = mutableListOf<QuoteLoadingUiEvent>()
        val loadingJob = launch { vm.quoteLoadingEvent.collect { loadingEvents.add(it) } }
        openResults(vm)
        assertEquals(listOf("initiate", "summary", "list"), repo.quoteApiCalls)

        vm.onAction(NewCarAction.OnUpdateDraftIdv(400_000f))
        vm.onAction(NewCarAction.ApplyFilter)
        advanceUntilIdle()

        assertEquals(listOf("initiate", "summary", "list", "initiate", "summary", "list"), repo.quoteApiCalls)
        assertEquals(400_000, repo.premiumInitiateInputs.last().vehicleExpectedIdv)
        assertTrue(vm.uiState.value.quoteState is QuoteFetchState.Success)
        // Still on the results screen: no second navigation
        assertEquals(1, loadingEvents.count { it is QuoteLoadingUiEvent.NavigateToQuotes })
        loadingJob.cancel()
    }

    @Test
    fun `applying the filter with the IDV unchanged makes no quote API call`() = runTest {
        val repo = idvRepository()
        val vm = NewCarViewModel(repo)
        openResults(vm)
        val callsBefore = repo.quoteApiCalls.toList()

        vm.onAction(NewCarAction.ApplyFilter)
        advanceUntilIdle()

        assertEquals(callsBefore, repo.quoteApiCalls)
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
        vm.onAction(NewCarAction.OnConfirmQuotePopup)
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
        vm.onAction(NewCarAction.OnConfirmQuotePopup)
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
