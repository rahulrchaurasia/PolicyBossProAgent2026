package com.policyboss.policybosspro.feature.carInsurance

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.Flow
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumQuotePollEvent
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumPollStatus
import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.feature.carInsurance.data.api.CarInsuranceApi
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteResponse
import com.policyboss.policybosspro.feature.carInsurance.data.repository.CarInsuranceRepository
import com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode
import com.policyboss.policybosspro.feature.carInsurance.model.CpaCoverageOption
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiEvent
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumInitiateInput
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumInitiateResult
import com.policyboss.policybosspro.feature.carInsurance.model.QuoteFetchState
import com.policyboss.policybosspro.feature.carInsurance.model.QuoteLoadingUiEvent
import com.policyboss.policybosspro.feature.carInsurance.viewmodel.NewCarViewModel
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumProgress
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumQuoteList
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
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
class NewCarQuoteLoadingTest {

    private val testDispatcher = StandardTestDispatcher()


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun fakeRepository(
        handler: suspend (CarQuoteRequest) -> Resource<CarQuoteResponse>
    ): CarInsuranceRepository = object : FakeCarInsuranceRepository() {
        override suspend fun getCarQuotes(request: CarQuoteRequest): Resource<CarQuoteResponse> =
            handler(request)
    }

    /** Car-number journey: quotes are started through premium_initiate. */
    private fun premiumRepository(
        quotes: suspend (String) -> Resource<PremiumQuoteList> = { Resource.Success(samplePremiumQuoteList(it)) },
        pollEvents: ((String) -> Flow<PremiumQuotePollEvent>)? = null,
        handler: suspend (PremiumInitiateInput) -> Resource<PremiumInitiateResult>
    ): FakeCarInsuranceRepository = object : FakeCarInsuranceRepository(
        premium = handler,
        premiumQuotes = quotes,
        premiumPollEvents = pollEvents
    ) {
        override suspend fun getCarQuotes(request: CarQuoteRequest): Resource<CarQuoteResponse> =
            error("car-number journey must not call getCarQuotes")
    }

    @Test
    fun `displayCustomerFirstName extracts first token and handles blank gracefully`() {
        assertEquals("Dhruvi", NewCarUiState(customerName = "  Dhruvi   Shah ").displayCustomerFirstName)
        assertEquals("Rahul", NewCarUiState(customerName = "Rahul").displayCustomerFirstName)
        assertEquals("", NewCarUiState(customerName = "   ").displayCustomerFirstName)
    }

    @Test
    fun `init preserves edited customer name on subsequent calls with same entryMode`() {
        val vm = NewCarViewModel(fakeRepository { Resource.Error("unused") })
        vm.initForMode(entryMode = CarEntryMode.WITHOUT_CAR_NUMBER)
        vm.onAction(NewCarAction.OnCustomerNameChange("Dhruvi Shah"))

        // Simulates returning to NewCarScreen when LaunchedEffect(entryMode) re-runs
        vm.initForMode(entryMode = CarEntryMode.WITHOUT_CAR_NUMBER)

        assertEquals("Dhruvi Shah", vm.uiState.value.customerName)
        assertEquals("Dhruvi", vm.uiState.value.displayCustomerFirstName)
    }

    @Test
    fun `get quotes navigates to loading screen and only navigates to results after API succeeds`() = runTest {
        val deferred = CompletableDeferred<Resource<PremiumInitiateResult>>()
        var callCount = 0
        val vm = NewCarViewModel(
            premiumRepository {
                callCount++
                deferred.await()
            }
        )
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        testDispatcher.scheduler.advanceUntilIdle()

        val formEvents = mutableListOf<NewCarUiEvent>()
        val loadingEvents = mutableListOf<QuoteLoadingUiEvent>()
        val formJob = launch { vm.uiEvent.collect { formEvents.add(it) } }
        val loadingJob = launch { vm.quoteLoadingEvent.collect { loadingEvents.add(it) } }

        // First click starts quote fetch
        vm.onAction(NewCarAction.OnGetQuotesClick)
        runCurrent()

        assertTrue(vm.uiState.value.quoteState is QuoteFetchState.Loading)
        assertTrue(vm.uiState.value.isLoading)
        assertEquals(listOf(NewCarUiEvent.NavigateToQuoteLoading), formEvents)
        assertTrue("Must not navigate to quotes before API returns", loadingEvents.isEmpty())
        assertEquals(1, callCount)

        // Repeated clicks while loading must be ignored
        vm.onAction(NewCarAction.OnGetQuotesClick)
        vm.onAction(NewCarAction.OnGetQuotesClick)
        runCurrent()
        assertEquals(1, callCount)
        assertEquals(1, formEvents.size)

        // Complete API successfully
        deferred.complete(Resource.Success(samplePremiumInitiateResult("11380101")))
        advanceUntilIdle()

        val successState = vm.uiState.value.quoteState as? QuoteFetchState.Success
        assertNotNull(successState)
        assertEquals("SRN-11380101", successState?.quoteId)
        assertEquals("11380101", vm.uiState.value.crnNumber)
        assertNotNull(vm.uiState.value.premiumInitiate)
        assertFalse(vm.uiState.value.isLoading)
        assertEquals(
            listOf(QuoteLoadingUiEvent.NavigateToQuotes("SRN-11380101", null)),
            loadingEvents
        )

        // Consuming navigation resets quoteState to Idle while preserving customer name
        vm.onAction(NewCarAction.OnQuoteNavigationHandled)
        assertEquals(QuoteFetchState.Idle, vm.uiState.value.quoteState)
        assertEquals("PRIYANKA VYAS", vm.uiState.value.customerName)

        formJob.cancel()
        loadingJob.cancel()
    }

    @Test
    fun `API failure transitions to Error state and retry recovers without duplicate loading navigation`() = runTest {
        var attempt = 0
        val vm = NewCarViewModel(
            premiumRepository {
                attempt++
                if (attempt == 1) {
                    Resource.Error("Quote service temporarily unavailable")
                } else {
                    Resource.Success(samplePremiumInitiateResult("2"))
                }
            }
        )
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        testDispatcher.scheduler.advanceUntilIdle()

        val formEvents = mutableListOf<NewCarUiEvent>()
        val loadingEvents = mutableListOf<QuoteLoadingUiEvent>()
        val formJob = launch { vm.uiEvent.collect { formEvents.add(it) } }
        val loadingJob = launch { vm.quoteLoadingEvent.collect { loadingEvents.add(it) } }

        vm.onAction(NewCarAction.OnGetQuotesClick)
        advanceUntilIdle()

        val errorState = vm.uiState.value.quoteState as? QuoteFetchState.Error
        assertNotNull(errorState)
        assertEquals("Quote service temporarily unavailable", errorState?.message)
        assertEquals(1, formEvents.count { it is NewCarUiEvent.NavigateToQuoteLoading })
        assertTrue(loadingEvents.isEmpty())

        // Retry stays on existing QuoteLoadingScreen and succeeds
        vm.onAction(NewCarAction.OnRetryGetQuotes)
        advanceUntilIdle()

        assertEquals(2, attempt)
        assertEquals(1, formEvents.count { it is NewCarUiEvent.NavigateToQuoteLoading })
        assertEquals(
            listOf(QuoteLoadingUiEvent.NavigateToQuotes("SRN-2", null)),
            loadingEvents
        )

        formJob.cancel()
        loadingJob.cancel()
    }

    @Test
    fun `cancelling quote loading cancels in-flight request, resets to Idle, and closes loading screen`() = runTest {
        val deferred = CompletableDeferred<Resource<PremiumInitiateResult>>()
        val vm = NewCarViewModel(premiumRepository { deferred.await() })
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        testDispatcher.scheduler.advanceUntilIdle()

        val loadingEvents = mutableListOf<QuoteLoadingUiEvent>()
        val loadingJob = launch { vm.quoteLoadingEvent.collect { loadingEvents.add(it) } }

        vm.onAction(NewCarAction.OnGetQuotesClick)
        runCurrent()
        assertTrue(vm.uiState.value.isLoading)

        vm.onAction(NewCarAction.OnCancelQuoteLoading)
        advanceUntilIdle()

        assertEquals(QuoteFetchState.Idle, vm.uiState.value.quoteState)
        assertFalse(vm.uiState.value.isLoading)
        assertEquals("PRIYANKA VYAS", vm.uiState.value.customerName)
        assertEquals(listOf(QuoteLoadingUiEvent.CloseLoading), loadingEvents)

        // Late completion after cancellation must not trigger quote navigation
        deferred.complete(Resource.Success(samplePremiumInitiateResult("LATE")))
        advanceUntilIdle()
        assertEquals(QuoteFetchState.Idle, vm.uiState.value.quoteState)
        assertEquals(listOf(QuoteLoadingUiEvent.CloseLoading), loadingEvents)

        loadingJob.cancel()
    }

    @Test
    fun `car-number journey sends vahan vehicle and form choices to premium_initiate`() = runTest {
        val repo = premiumRepository { Resource.Success(samplePremiumInitiateResult()) }
        val vm = NewCarViewModel(repo)
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        advanceUntilIdle()
        vm.onAction(NewCarAction.OnGetQuotesClick)
        advanceUntilIdle()

        val input = repo.premiumInitiateInputs.single()
        assertEquals("RJ14-1234", input.vehicle.registrationNumber)
        assertEquals("PRIYANKA VYAS", input.customerName)
        assertTrue(vm.uiState.value.quoteState is QuoteFetchState.Success)

        // Results screen must not bounce back while the premium_initiate result is held
        val formEvents = mutableListOf<NewCarUiEvent>()
        val formJob = launch { vm.uiEvent.collect { formEvents.add(it) } }
        vm.ensureQuotesLoaded()
        advanceUntilIdle()
        assertFalse(formEvents.any { it is NewCarUiEvent.NavigateBackToForm })
        formJob.cancel()
    }

    @Test
    fun `premium_initiate search reference loads insurer quotes before opening results`() = runTest {
        val repo = premiumRepository { Resource.Success(samplePremiumInitiateResult("11380101")) }
        val vm = NewCarViewModel(repo)
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        advanceUntilIdle()
        val loadingEvents = mutableListOf<QuoteLoadingUiEvent>()
        val loadingJob = launch { vm.quoteLoadingEvent.collect { loadingEvents.add(it) } }

        vm.onAction(NewCarAction.OnGetQuotesClick)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(listOf("SRN-11380101"), repo.premiumQuoteSearches)
        assertEquals(listOf("ARN-IFFCO", "ARN-BAJAJ"), state.quotes.map { it.id })
        assertTrue("cheapest quote is recommended", state.quotes.first().isRecommended)
        assertEquals("ARN-IFFCO", state.recommendedQuote?.id)
        assertEquals(2471, state.minPremium)
        assertEquals(listOf("Future Generali"), state.declinedInsurers.map { it.insurerName })
        assertFalse(state.isAwaitingInsurerQuotes)
        assertEquals(null, state.premiumProgress)
        assertEquals(listOf(QuoteLoadingUiEvent.NavigateToQuotes("SRN-11380101", null)), loadingEvents)
        loadingJob.cancel()
    }

    @Test
    fun `premium_list_db failure shows the loader error and does not open results`() = runTest {
        val repo = premiumRepository(quotes = { Resource.Error("Unable to fetch quotes (500). Please try again.") }) {
            Resource.Success(samplePremiumInitiateResult())
        }
        val vm = NewCarViewModel(repo)
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        advanceUntilIdle()
        val loadingEvents = mutableListOf<QuoteLoadingUiEvent>()
        val loadingJob = launch { vm.quoteLoadingEvent.collect { loadingEvents.add(it) } }

        vm.onAction(NewCarAction.OnGetQuotesClick)
        advanceUntilIdle()

        assertEquals(
            QuoteFetchState.Error("Unable to fetch quotes (500). Please try again."),
            vm.uiState.value.quoteState
        )
        assertTrue(loadingEvents.none { it is QuoteLoadingUiEvent.NavigateToQuotes })
        loadingJob.cancel()
    }

    /** Two premium_list_db snapshots: IFFCO first (pending), then IFFCO + Bajaj (complete). */
    private fun progressivePoll(secondSnapshot: CompletableDeferred<Unit>): (String) -> Flow<PremiumQuotePollEvent> = { srn ->
        flow {
            val full = samplePremiumQuoteList(srn)
            val first = full.copy(quotes = full.quotes.take(1), progress = PremiumProgress("pending", total = 3, pending = 2))
            emit(PremiumQuotePollEvent.Update(first, PremiumPollStatus.POLLING))
            secondSnapshot.await()
            emit(PremiumQuotePollEvent.Update(full, PremiumPollStatus.COMPLETE))
        }
    }

    @Test
    fun `results open on the first premium_list_db answer and grow as insurers respond`() = runTest {
        val second = CompletableDeferred<Unit>()
        val repo = premiumRepository(pollEvents = progressivePoll(second)) { Resource.Success(samplePremiumInitiateResult("11380101")) }
        val vm = NewCarViewModel(repo)
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        advanceUntilIdle()
        val loadingEvents = mutableListOf<QuoteLoadingUiEvent>()
        val loadingJob = launch { vm.quoteLoadingEvent.collect { loadingEvents.add(it) } }

        vm.onAction(NewCarAction.OnGetQuotesClick)
        advanceUntilIdle()

        // First snapshot: results open with what has arrived, polling continues
        assertEquals(listOf("ARN-IFFCO"), vm.uiState.value.quotes.map { it.id })
        assertTrue(vm.uiState.value.isFetchingMoreQuotes)
        assertEquals("1 of 3 insurers responded", vm.uiState.value.premiumProgressText)
        assertEquals(1, loadingEvents.count { it is QuoteLoadingUiEvent.NavigateToQuotes })
        vm.onAction(NewCarAction.OnQuoteNavigationHandled)

        // Second snapshot: the list grows in place, no second navigation
        second.complete(Unit)
        advanceUntilIdle()
        assertEquals(listOf("ARN-IFFCO", "ARN-BAJAJ"), vm.uiState.value.quotes.map { it.id })
        assertFalse(vm.uiState.value.isFetchingMoreQuotes)
        assertEquals(PremiumPollStatus.COMPLETE, vm.uiState.value.premiumPollStatus)
        assertEquals(1, loadingEvents.count { it is QuoteLoadingUiEvent.NavigateToQuotes })
        assertEquals("11380101", vm.uiState.value.crnNumber)
        loadingJob.cancel()
    }

    @Test
    fun `results open on an empty first answer and the count grows as plans arrive`() = runTest {
        val second = CompletableDeferred<Unit>()
        val poll: (String) -> Flow<PremiumQuotePollEvent> = { srn ->
            flow {
                val full = samplePremiumQuoteList(srn)
                // First answer: insurers still working, no valid plan yet
                val empty = full.copy(quotes = emptyList(), progress = PremiumProgress("pending", total = 18, pending = 15))
                emit(PremiumQuotePollEvent.Update(empty, PremiumPollStatus.POLLING))
                second.await()
                emit(PremiumQuotePollEvent.Update(full, PremiumPollStatus.POLLING))
            }
        }
        val repo = premiumRepository(pollEvents = poll) { Resource.Success(samplePremiumInitiateResult()) }
        val vm = NewCarViewModel(repo)
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        advanceUntilIdle()
        val loadingEvents = mutableListOf<QuoteLoadingUiEvent>()
        val loadingJob = launch { vm.quoteLoadingEvent.collect { loadingEvents.add(it) } }

        vm.onAction(NewCarAction.OnGetQuotesClick)
        advanceUntilIdle()

        // Results open on the first answer; the summary shows its spinner while no plan exists
        assertEquals(1, loadingEvents.count { it is QuoteLoadingUiEvent.NavigateToQuotes })
        assertEquals(0, vm.uiState.value.totalQuotesCount)
        assertTrue(vm.uiState.value.isFetchingMoreQuotes)
        assertTrue(vm.uiState.value.isQuoteSummaryLoading)

        second.complete(Unit)
        advanceUntilIdle()

        // Plans arrive: spinner hidden, count grows, values bound, no second navigation
        assertFalse(vm.uiState.value.isQuoteSummaryLoading)
        assertEquals(2, vm.uiState.value.totalQuotesCount)
        assertEquals(2471, vm.uiState.value.minPremium)
        assertEquals(1, loadingEvents.count { it is QuoteLoadingUiEvent.NavigateToQuotes })
        loadingJob.cancel()
    }

    @Test
    fun `leaving results stops polling and ignores later snapshots`() = runTest {
        val second = CompletableDeferred<Unit>()
        val repo = premiumRepository(pollEvents = progressivePoll(second)) { Resource.Success(samplePremiumInitiateResult()) }
        val vm = NewCarViewModel(repo)
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        advanceUntilIdle()
        vm.onAction(NewCarAction.OnGetQuotesClick)
        advanceUntilIdle()
        assertTrue(vm.uiState.value.isFetchingMoreQuotes)

        vm.onAction(NewCarAction.OnLeaveQuoteResults)
        second.complete(Unit)
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isFetchingMoreQuotes)
        assertEquals(listOf("ARN-IFFCO"), vm.uiState.value.quotes.map { it.id })
    }

    @Test
    fun `get quotes again while polling starts a new search`() = runTest {
        val second = CompletableDeferred<Unit>()
        val repo = premiumRepository(pollEvents = progressivePoll(second)) { Resource.Success(samplePremiumInitiateResult()) }
        val vm = NewCarViewModel(repo)
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        advanceUntilIdle()
        vm.onAction(NewCarAction.OnGetQuotesClick)
        advanceUntilIdle()
        vm.onAction(NewCarAction.OnQuoteNavigationHandled)

        vm.onAction(NewCarAction.OnGetQuotesClick)
        advanceUntilIdle()

        assertEquals(2, repo.premiumInitiateInputs.size)
        assertEquals(2, repo.premiumQuoteSearches.size)
    }

    @Test
    fun `zextra switch follows the CPA choice when real quotes load`() = runTest {
        val repo = premiumRepository { Resource.Success(samplePremiumInitiateResult()) }
        val vm = NewCarViewModel(repo)
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        advanceUntilIdle()
        vm.onAction(NewCarAction.OnGetQuotesClick)
        advanceUntilIdle()

        // For Individual registration, default CPA choice is Zextra Switch (ON)
        assertTrue(vm.uiState.value.isZextraBundleEnabled)
        assertEquals(CpaCoverageOption.ZEXTRA_SWITCH, vm.uiState.value.cpaCoverageOption)

        // Switching to Insurer CPA or No CPA disables Zextra bundle
        vm.onAction(NewCarAction.OnSelectCpaCoverageOption(CpaCoverageOption.INSURER_CPA))
        assertFalse(vm.uiState.value.isZextraBundleEnabled)

        vm.onAction(NewCarAction.OnSelectCpaCoverageOption(CpaCoverageOption.NO_CPA))
        assertFalse(vm.uiState.value.isZextraBundleEnabled)
    }

    @Test
    fun `premium progress text counts answered insurers`() {
        val state = NewCarUiState(premiumProgress = PremiumProgress(status = "pending", total = 18, pending = 12))
        assertEquals("6 of 18 insurers responded", state.premiumProgressText)
        assertEquals(null, NewCarUiState().premiumProgressText)
    }

    @Test
    fun `invalid form blocks quote submission and shows validation feedback`() = runTest {
        var called = false
        val vm = NewCarViewModel(
            fakeRepository {
                called = true
                Resource.Success(CarQuoteResponse())
            }
        )
        vm.initForMode(entryMode = CarEntryMode.WITHOUT_CAR_NUMBER)
        vm.onAction(NewCarAction.OnCustomerNameChange("   "))

        val formEvents = mutableListOf<NewCarUiEvent>()
        val formJob = launch { vm.uiEvent.collect { formEvents.add(it) } }

        vm.onAction(NewCarAction.OnGetQuotesClick)
        advanceUntilIdle()

        assertFalse(called)
        assertEquals(QuoteFetchState.Idle, vm.uiState.value.quoteState)
        assertNotNull(vm.uiState.value.customerNameError)
        assertTrue(formEvents.any { it is NewCarUiEvent.ShowSnackbar })
        assertFalse(formEvents.any { it is NewCarUiEvent.NavigateToQuoteLoading })

        formJob.cancel()
    }

    @Test
    fun `init with car number configures WITH_CAR_NUMBER mode, Vahan data, and pre-filled vehicle details`() {
        val vm = NewCarViewModel(fakeRepository { Resource.Success(CarQuoteResponse()) })
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(CarEntryMode.WITH_CAR_NUMBER, state.entryMode)
        assertEquals("Continue with Car Number", state.headerTitle)
        assertTrue(state.showVahanBanner)
        assertTrue(state.vahanData.contains("KIA INDIA PRIVATE LIMITED"))
        assertEquals("RJ14-1234", state.registrationNumber)
        assertEquals("PRIYANKA VYAS", state.customerName)
        assertEquals("RJ-14", state.selectedRto?.value)
        assertEquals("Jan", state.manufacturingMonth)
        assertEquals("2021", state.manufacturingYear)
        assertTrue(state.hasExistingPolicy)
        assertEquals("ORIENTAL", state.selectedInsurer?.value)
        assertTrue(state.isFormValid)
    }

    @Test
    fun `init without car number configures WITHOUT_CAR_NUMBER mode, hides Vahan banner and resets vehicle details`() {
        val vm = NewCarViewModel(fakeRepository { Resource.Success(CarQuoteResponse()) })
        vm.initForMode(entryMode = CarEntryMode.WITHOUT_CAR_NUMBER)

        val state = vm.uiState.value
        assertEquals(CarEntryMode.WITHOUT_CAR_NUMBER, state.entryMode)
        assertEquals("Continue without car number", state.headerTitle)
        assertFalse(state.showVahanBanner)
        assertEquals("", state.vahanData)
        assertEquals("", state.registrationNumber)
        assertTrue(state.hasExistingPolicy)
    }

    @Test
    fun `update manufacturing month and year modifies ui state accordingly`() {
        val vm = NewCarViewModel(fakeRepository { Resource.Success(CarQuoteResponse()) })
        vm.initForMode(entryMode = CarEntryMode.WITHOUT_CAR_NUMBER)

        vm.onAction(NewCarAction.UpdateManufacturingMonth("Mar"))
        vm.onAction(NewCarAction.UpdateManufacturingYear("2023"))

        assertEquals("Mar", vm.uiState.value.manufacturingMonth)
        assertEquals("2023", vm.uiState.value.manufacturingYear)
    }

    @Test
    fun `cancelling quote loading allows re-entry from Home with same registration number without blocking`() = runTest {
        val vm = NewCarViewModel(fakeRepository { Resource.Success(CarQuoteResponse()) })
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        testDispatcher.scheduler.advanceUntilIdle()

        // User cancels on loading screen
        vm.onAction(NewCarAction.OnCancelQuoteLoading)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(QuoteFetchState.Idle, vm.uiState.value.quoteState)

        // Re-entering from Home with same registration number
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("RJ14-1234", vm.uiState.value.registrationNumber)
    }
}

