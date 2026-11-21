package com.policyboss.policybosspro.feature.carInsurance

import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.feature.carInsurance.data.api.CarInsuranceApi
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteResponse
import com.policyboss.policybosspro.feature.carInsurance.data.repository.CarInsuranceRepository
import com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiEvent
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState
import com.policyboss.policybosspro.feature.carInsurance.model.QuoteFetchState
import com.policyboss.policybosspro.feature.carInsurance.model.QuoteLoadingUiEvent
import com.policyboss.policybosspro.feature.carInsurance.viewmodel.NewCarViewModel
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

    private val dummyApi = object : CarInsuranceApi {
        override suspend fun getCarQuotes(request: CarQuoteRequest): Response<CarQuoteResponse> {
            error("Fake repository overrides getCarQuotes directly")
        }
    }

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
    ): CarInsuranceRepository = object : CarInsuranceRepository(dummyApi) {
        override suspend fun getCarQuotes(request: CarQuoteRequest): Resource<CarQuoteResponse> =
            handler(request)
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
        val deferred = CompletableDeferred<Resource<CarQuoteResponse>>()
        var callCount = 0
        val vm = NewCarViewModel(
            fakeRepository {
                callCount++
                deferred.await()
            }
        )
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")

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
        deferred.complete(
            Resource.Success(
                CarQuoteResponse(
                    status = "SUCCESS",
                    quoteId = "Q-101",
                    quoteUrl = "https://www.policyboss.com/quotes/Q-101",
                    quotesCount = 12
                )
            )
        )
        advanceUntilIdle()

        val successState = vm.uiState.value.quoteState as? QuoteFetchState.Success
        assertNotNull(successState)
        assertEquals("Q-101", successState?.quoteId)
        assertEquals(12, successState?.quotesCount)
        assertFalse(vm.uiState.value.isLoading)
        assertEquals(
            listOf(QuoteLoadingUiEvent.NavigateToQuotes("Q-101", "https://www.policyboss.com/quotes/Q-101")),
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
            fakeRepository {
                attempt++
                if (attempt == 1) {
                    Resource.Error("Quote service temporarily unavailable")
                } else {
                    Resource.Success(
                        CarQuoteResponse(
                            status = "SUCCESS",
                            quoteId = "Q-RETRY-2",
                            quoteUrl = "https://www.policyboss.com/quotes/Q-RETRY-2",
                            quotesCount = 6
                        )
                    )
                }
            }
        )
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")

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
            listOf(QuoteLoadingUiEvent.NavigateToQuotes("Q-RETRY-2", "https://www.policyboss.com/quotes/Q-RETRY-2")),
            loadingEvents
        )

        formJob.cancel()
        loadingJob.cancel()
    }

    @Test
    fun `cancelling quote loading cancels in-flight request, resets to Idle, and closes loading screen`() = runTest {
        val deferred = CompletableDeferred<Resource<CarQuoteResponse>>()
        val vm = NewCarViewModel(fakeRepository { deferred.await() })
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")

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
        deferred.complete(
            Resource.Success(CarQuoteResponse(status = "SUCCESS", quoteId = "LATE", quoteUrl = "https://example.com"))
        )
        advanceUntilIdle()
        assertEquals(QuoteFetchState.Idle, vm.uiState.value.quoteState)
        assertEquals(listOf(QuoteLoadingUiEvent.CloseLoading), loadingEvents)

        loadingJob.cancel()
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
}

