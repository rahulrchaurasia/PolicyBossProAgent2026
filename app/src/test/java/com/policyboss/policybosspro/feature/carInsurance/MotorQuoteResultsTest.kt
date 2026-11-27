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
import com.policyboss.policybosspro.feature.carInsurance.model.VehicleInfoDomain
import com.policyboss.policybosspro.feature.carInsurance.model.toPremiumInitiateInput
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

    @Test
    fun `default CPA toggle is ZEXTRA_SWITCH for Individual and NO_CPA for Company`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode.WITHOUT_CAR_NUMBER)

        // Default is Individual
        assertEquals(com.policyboss.policybosspro.feature.carInsurance.model.OwnershipType.INDIVIDUAL, vm.uiState.value.ownershipType)
        assertEquals(com.policyboss.policybosspro.feature.carInsurance.model.CpaCoverageOption.ZEXTRA_SWITCH, vm.uiState.value.cpaCoverageOption)
        assertTrue(vm.uiState.value.isZextraBundleEnabled)

        // Select Company
        vm.onAction(NewCarAction.OnOwnershipSelected(com.policyboss.policybosspro.feature.carInsurance.model.OwnershipType.COMPANY))
        assertEquals(com.policyboss.policybosspro.feature.carInsurance.model.OwnershipType.COMPANY, vm.uiState.value.ownershipType)
        assertEquals(com.policyboss.policybosspro.feature.carInsurance.model.CpaCoverageOption.NO_CPA, vm.uiState.value.cpaCoverageOption)
        assertFalse(vm.uiState.value.isZextraBundleEnabled)
        assertFalse(vm.uiState.value.draftQuoteFilter.selectedLiabilityCoverIds.contains("owner_driver_pa"))
        assertFalse(vm.uiState.value.appliedQuoteFilter.selectedLiabilityCoverIds.contains("owner_driver_pa"))

        // Switching back to Individual restores ZEXTRA_SWITCH
        vm.onAction(NewCarAction.OnOwnershipSelected(com.policyboss.policybosspro.feature.carInsurance.model.OwnershipType.INDIVIDUAL))
        assertEquals(com.policyboss.policybosspro.feature.carInsurance.model.OwnershipType.INDIVIDUAL, vm.uiState.value.ownershipType)
        assertEquals(com.policyboss.policybosspro.feature.carInsurance.model.CpaCoverageOption.ZEXTRA_SWITCH, vm.uiState.value.cpaCoverageOption)
        assertTrue(vm.uiState.value.isZextraBundleEnabled)
    }

    @Test
    fun `Company registration prevents selecting owner_driver_pa in filter`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode.WITHOUT_CAR_NUMBER)
        vm.onAction(NewCarAction.OnOwnershipSelected(com.policyboss.policybosspro.feature.carInsurance.model.OwnershipType.COMPANY))

        // Attempt to toggle owner_driver_pa
        vm.onAction(NewCarAction.OnToggleDraftLiabilityCover("owner_driver_pa"))
        assertFalse(vm.uiState.value.draftQuoteFilter.selectedLiabilityCoverIds.contains("owner_driver_pa"))

        // Can toggle other covers
        vm.onAction(NewCarAction.OnToggleDraftLiabilityCover("paid_driver_liability"))
        assertTrue(vm.uiState.value.draftQuoteFilter.selectedLiabilityCoverIds.contains("paid_driver_liability"))
    }

    @Test
    fun `Insurer CPA selected for Individual auto-selects owner_driver_pa and No CPA removes it`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode.WITHOUT_CAR_NUMBER)

        // Switch to Insurer CPA -> owner_driver_pa is added to both draft and applied filters
        vm.onAction(
            NewCarAction.OnSelectCpaCoverageOption(
                com.policyboss.policybosspro.feature.carInsurance.model.CpaCoverageOption.INSURER_CPA
            )
        )
        assertTrue(vm.uiState.value.draftQuoteFilter.selectedLiabilityCoverIds.contains("owner_driver_pa"))
        assertTrue(vm.uiState.value.appliedQuoteFilter.selectedLiabilityCoverIds.contains("owner_driver_pa"))

        // Switch to No CPA -> owner_driver_pa is removed
        vm.onAction(
            NewCarAction.OnSelectCpaCoverageOption(
                com.policyboss.policybosspro.feature.carInsurance.model.CpaCoverageOption.NO_CPA
            )
        )
        assertFalse(vm.uiState.value.draftQuoteFilter.selectedLiabilityCoverIds.contains("owner_driver_pa"))
        assertFalse(vm.uiState.value.appliedQuoteFilter.selectedLiabilityCoverIds.contains("owner_driver_pa"))
    }

    @Test
    fun `two-way CPA sync updates toggle when owner_driver_pa is unchecked or checked and applied`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode.WITHOUT_CAR_NUMBER)

        // Start with Insurer CPA (owner_driver_pa selected)
        vm.onAction(
            NewCarAction.OnSelectCpaCoverageOption(
                com.policyboss.policybosspro.feature.carInsurance.model.CpaCoverageOption.INSURER_CPA
            )
        )
        assertTrue(vm.uiState.value.appliedQuoteFilter.selectedLiabilityCoverIds.contains("owner_driver_pa"))

        // Uncheck owner_driver_pa in filter and Apply -> reverts CPA toggle to NO_CPA
        vm.onAction(NewCarAction.OnToggleDraftLiabilityCover("owner_driver_pa"))
        vm.onAction(NewCarAction.ApplyFilter)
        assertEquals(
            com.policyboss.policybosspro.feature.carInsurance.model.CpaCoverageOption.NO_CPA,
            vm.uiState.value.cpaCoverageOption
        )

        // Check owner_driver_pa in filter and Apply -> sets CPA toggle to INSURER_CPA
        vm.onAction(NewCarAction.OnToggleDraftLiabilityCover("owner_driver_pa"))
        vm.onAction(NewCarAction.ApplyFilter)
        assertEquals(
            com.policyboss.policybosspro.feature.carInsurance.model.CpaCoverageOption.INSURER_CPA,
            vm.uiState.value.cpaCoverageOption
        )
    }

    @Test
    fun `IDV bounds and expected IDV are populated in toPremiumInitiateInput when explicitly changed`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode.WITHOUT_CAR_NUMBER)

        val vehicle = VehicleInfoDomain(
            registrationNumber = "MH01BB0197",
            vahanSummary = "",
            ownerName = "Test",
            makeName = "Maruti",
            modelName = "Wagon R",
            variantName = "VXI",
            fuelType = "Petrol",
            modelId = "138",
            variantId = "6917",
            fuelId = "1",
            cubicCapacity = "998",
            rtoCode = "291",
            rtoName = "Mumbai",
            registrationDate = "18-Oct-2011",
            manufacturingMonth = "Sep",
            manufacturingYear = "2011",
            previousInsurerId = "",
            previousInsurerName = "",
            policyExpiryDate = "",
            vehicleDetailId = ""
        )

        // Initially not explicitly changed -> vehicleExpectedIdv = 0
        assertFalse(vm.uiState.value.appliedQuoteFilter.isIdvExplicitlyChanged)
        val initialInput = vm.uiState.value.toPremiumInitiateInput(vehicle)
        assertEquals(0, initialInput.vehicleExpectedIdv)

        // Update draft IDV and Apply -> vehicleExpectedIdv = selected IDV
        vm.onAction(NewCarAction.OnUpdateDraftIdv(125000f))
        vm.onAction(NewCarAction.ApplyFilter)
        assertTrue(vm.uiState.value.appliedQuoteFilter.isIdvExplicitlyChanged)
        assertEquals(125000f, vm.uiState.value.appliedQuoteFilter.selectedIdv)
        val updatedInput = vm.uiState.value.toPremiumInitiateInput(vehicle)
        assertEquals(125000, updatedInput.vehicleExpectedIdv)
    }
}

