package com.policyboss.policybosspro.feature.carInsurance

import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.feature.carInsurance.data.api.CarInsuranceApi
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteResponse
import com.policyboss.policybosspro.feature.carInsurance.data.repository.CarInsuranceRepository
import com.policyboss.policybosspro.feature.carInsurance.model.CarBrandModelItem
import com.policyboss.policybosspro.feature.carInsurance.model.CarDetailsStep
import com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode
import com.policyboss.policybosspro.feature.carInsurance.model.CarVariantItem
import com.policyboss.policybosspro.feature.carInsurance.model.NavigationDirection
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.model.PolicyType
import com.policyboss.policybosspro.feature.carInsurance.viewmodel.NewCarViewModel
import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class NewCarViewModelTest {

    private val testDispatcher = StandardTestDispatcher()


    private fun fakeRepository(): CarInsuranceRepository = object : FakeCarInsuranceRepository() {
        override suspend fun getCarQuotes(request: CarQuoteRequest): Resource<CarQuoteResponse> =
            Resource.Success(CarQuoteResponse())
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
    fun `tp only with existing policy shows expiry date and previous insurer but hides tp insurer and claims`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        testDispatcher.scheduler.advanceUntilIdle()

        // Switch to TP Only
        vm.onAction(NewCarAction.OnPolicyTypeSelected("TP Only"))
        vm.onAction(NewCarAction.OnExistingPolicyToggle(true))

        val state = vm.uiState.value
        assertEquals(PolicyType.TP_ONLY, state.policyType)
        assertEquals("Do You Have Existing Policy?", state.existingPolicyQuestionLabel)
        assertTrue(state.shouldShowExistingPolicySection)
        assertTrue(state.shouldShowExistingPolicyCard)
        assertTrue(state.shouldShowExpiryDate)
        assertTrue(state.shouldShowPreviousInsurer)
        assertFalse(state.shouldShowTpInsurer)
        assertFalse(state.shouldShowClaimAndNcb)

        // Missing previous insurer DOES invalidate form in TP Only mode when existing policy is true
        val stateWithoutInsurer = state.copy(selectedInsurer = null, policyExpiryDate = "29-Jan-2027")
        assertFalse(stateWithoutInsurer.isFormValid)

        // Missing expiry date DOES invalidate form
        val stateWithoutExpiry = state.copy(selectedInsurer = DropdownOption("ORIENTAL", "ORIENTAL"), policyExpiryDate = "")
        assertFalse(stateWithoutExpiry.isFormValid)
    }

    @Test
    fun `tp only without existing policy hides all rollover fields`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        testDispatcher.scheduler.advanceUntilIdle()

        vm.onAction(NewCarAction.OnPolicyTypeSelected("TP Only"))
        vm.onAction(NewCarAction.OnExistingPolicyToggle(false))

        val state = vm.uiState.value
        assertTrue(state.shouldShowExistingPolicySection)
        assertFalse(state.shouldShowExistingPolicyCard)
        assertFalse(state.shouldShowExpiryDate)
        assertFalse(state.shouldShowPreviousInsurer)
        assertFalse(state.shouldShowTpInsurer)
        assertFalse(state.shouldShowClaimAndNcb)
        assertTrue(state.isFormValid)
    }

    @Test
    fun `od only with existing policy shows expiry date, previous insurer, tp insurer, claim and ncb`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        testDispatcher.scheduler.advanceUntilIdle()

        // Switch to OD Only
        vm.onAction(NewCarAction.SelectPolicyType(PolicyType.OD_ONLY))
        vm.onAction(NewCarAction.OnExistingPolicyToggle(true))
        vm.onAction(NewCarAction.OnTpInsurerSelected(DropdownOption("ACKO", "Acko General Insurance")))

        val state = vm.uiState.value
        assertEquals(PolicyType.OD_ONLY, state.policyType)
        assertEquals("Do You Have Existing Comprehensive Policy?", state.existingPolicyQuestionLabel)
        assertTrue(state.shouldShowExistingPolicySection)
        assertTrue(state.shouldShowExistingPolicyCard)
        assertTrue(state.shouldShowExpiryDate)
        assertTrue(state.shouldShowPreviousInsurer)
        assertTrue(state.shouldShowTpInsurer)
        assertTrue(state.shouldShowClaimAndNcb)

        // Missing Previous Insurer DOES invalidate OD Only form when existing policy is true
        val stateWithoutPrevInsurer = state.copy(
            selectedInsurer = null,
            selectedTpInsurer = DropdownOption("ACKO", "Acko General Insurance"),
            policyExpiryDate = "29-Jan-2027"
        )
        assertFalse(stateWithoutPrevInsurer.isFormValid)

        // Missing Policy TP Insurer DOES invalidate OD Only form
        val stateWithoutTpInsurer = state.copy(selectedTpInsurer = null)
        assertFalse(stateWithoutTpInsurer.isFormValid)
    }

    @Test
    fun `od only without existing policy keeps card and tp insurer visible but hides expiry, previous insurer and claims`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        testDispatcher.scheduler.advanceUntilIdle()

        vm.onAction(NewCarAction.SelectPolicyType(PolicyType.OD_ONLY))
        vm.onAction(NewCarAction.OnExistingPolicyToggle(false))
        // The vahan lookup has no TP insurer, so the user picks one
        vm.onAction(NewCarAction.OnTpInsurerSelected(DropdownOption("ACKO", "Acko General Insurance")))

        val state = vm.uiState.value
        assertTrue(state.shouldShowExistingPolicySection)
        assertTrue(state.shouldShowExistingPolicyCard) // Card stays visible!
        assertTrue(state.shouldShowTpInsurer) // Policy TP Insurer stays visible!
        assertFalse(state.shouldShowExpiryDate)
        assertFalse(state.shouldShowPreviousInsurer)
        assertFalse(state.shouldShowClaimAndNcb)

        // With the TP insurer selected, the form is valid
        assertTrue(state.isFormValid)

        // Missing TP insurer in OD + No DOES invalidate form!
        val stateWithoutTp = state.copy(selectedTpInsurer = null)
        assertFalse(stateWithoutTp.isFormValid)
    }

    @Test
    fun `comprehensive with existing policy shows expiry date, previous insurer, claim and ncb but hides tp insurer`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        testDispatcher.scheduler.advanceUntilIdle()

        // Default mode is Comprehensive
        vm.onAction(NewCarAction.SelectPolicyType(PolicyType.COMPREHENSIVE))
        vm.onAction(NewCarAction.OnExistingPolicyToggle(true))

        val state = vm.uiState.value
        assertEquals(PolicyType.COMPREHENSIVE, state.policyType)
        assertEquals("Do You Have Existing Comprehensive Policy?", state.existingPolicyQuestionLabel)
        assertTrue(state.shouldShowExistingPolicySection)
        assertTrue(state.shouldShowExistingPolicyCard)
        assertTrue(state.shouldShowExpiryDate)
        assertTrue(state.shouldShowPreviousInsurer)
        assertFalse(state.shouldShowTpInsurer)
        assertTrue(state.shouldShowClaimAndNcb)

        // Missing TP insurer should NOT invalidate Comprehensive form
        val stateWithoutTpInsurer = state.copy(
            selectedInsurer = DropdownOption("ORIENTAL", "ORIENTAL INSURANCE"),
            selectedTpInsurer = null,
            policyExpiryDate = "29-Jan-2027"
        )
        assertTrue(stateWithoutTpInsurer.isFormValid)

        // Missing Previous Insurer DOES invalidate Comprehensive form
        val stateWithoutPrevInsurer = stateWithoutTpInsurer.copy(selectedInsurer = null)
        assertFalse(stateWithoutPrevInsurer.isFormValid)
    }

    @Test
    fun `comprehensive without existing policy hides all rollover fields`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        testDispatcher.scheduler.advanceUntilIdle()

        vm.onAction(NewCarAction.SelectPolicyType(PolicyType.COMPREHENSIVE))
        vm.onAction(NewCarAction.OnExistingPolicyToggle(false))

        val state = vm.uiState.value
        assertTrue(state.shouldShowExistingPolicySection)
        assertFalse(state.shouldShowExistingPolicyCard)
        assertFalse(state.shouldShowExpiryDate)
        assertFalse(state.shouldShowPreviousInsurer)
        assertFalse(state.shouldShowTpInsurer)
        assertFalse(state.shouldShowClaimAndNcb)
        assertTrue(state.isFormValid)
    }

    @Test
    fun `claim toggle to yes automatically locks ncb to zero percent`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        testDispatcher.scheduler.advanceUntilIdle()

        // First select 25% NCB
        vm.onAction(NewCarAction.OnNcbSelected(DropdownOption("25", "25%")))
        assertEquals("25%", vm.uiState.value.selectedNcb?.label)

        // Toggle claim to Yes
        vm.onAction(NewCarAction.OnClaimToggle(hasClaim = true))
        assertTrue(vm.uiState.value.hasClaim)
        assertEquals("0%", vm.uiState.value.selectedNcb?.label)

        // Toggle claim to No allows custom NCB selection
        vm.onAction(NewCarAction.OnClaimToggle(hasClaim = false))
        assertFalse(vm.uiState.value.hasClaim)
        vm.onAction(NewCarAction.OnNcbSelected(DropdownOption("50", "50%")))
        assertEquals("50%", vm.uiState.value.selectedNcb?.label)
    }

    @Test
    fun `mode A WITH_CAR_NUMBER configures header banner prefilled vehicle and rollover`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(CarEntryMode.WITH_CAR_NUMBER, state.entryMode)
        assertEquals("Continue with Car Number", state.headerTitle)
        assertTrue(state.showVahanBanner)
        assertTrue(state.carDetailsSummary.isNotBlank())
        assertEquals("PRIYANKA VYAS", state.customerName)
        assertEquals("RJ-14", state.selectedRto?.value)
        assertTrue(state.shouldShowExistingPolicySection)
        assertTrue(state.isFormValid)
    }

    @Test
    fun `mode B WITHOUT_CAR_NUMBER hides banner and details, keeps rollover, validates vehicle specs`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(entryMode = CarEntryMode.WITHOUT_CAR_NUMBER)

        val state = vm.uiState.value
        assertEquals(CarEntryMode.WITHOUT_CAR_NUMBER, state.entryMode)
        assertEquals("Continue without car number", state.headerTitle)
        assertFalse(state.showVahanBanner)
        assertEquals("", state.carDetailsSummary)
        assertEquals("", state.customerName)
        assertNull(state.selectedRto)
        assertNull(state.selectedModel)
        assertNull(state.selectedVariant)
        assertNull(state.selectedFuel)
        assertTrue(state.shouldShowExistingPolicySection)

        // Empty state is not valid
        assertFalse(state.isFormValid)

        // Fill required fields
        val validState = state.copy(
            customerName = "Rahul Sharma",
            selectedRto = DropdownOption("MH-01", "MH01 - Mumbai"),
            selectedModel = DropdownOption("HONDA_AMAZE", "Honda Amaze"),
            selectedVariant = DropdownOption("1.2_S_CVT", "1.2 S CVT (1198CC)"),
            selectedFuel = DropdownOption("PETROL", "Petrol"),
            registrationDate = "15-Aug-2022",
            hasExistingPolicy = false
        )
        assertTrue(validState.isFormValid)
    }

    @Test
    fun `mode C NEW_CAR auto-populates current date, sets default OD+TP policy, completely hides rollover`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(entryMode = CarEntryMode.NEW_CAR)

        val state = vm.uiState.value
        assertEquals(CarEntryMode.NEW_CAR, state.entryMode)
        assertEquals("New Car", state.headerTitle)
        assertFalse(state.showVahanBanner)
        assertEquals("", state.carDetailsSummary)
        assertEquals("OD (1 Yr) + TP (3 Yr)", state.selectedPolicyType)

        // Verify dates match current system date
        val today = LocalDate.now()
        val expectedRegDate = today.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH))
        val expectedMonth = today.format(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH))
        val expectedYear = today.year.toString()

        assertEquals(expectedRegDate, state.registrationDate)
        assertEquals(expectedMonth, state.manufacturingMonth)
        assertEquals(expectedYear, state.manufacturingYear)

        // Rollover section is completely hidden
        assertFalse(state.shouldShowExistingPolicySection)
        assertFalse(state.shouldShowExpiryDate)
        assertFalse(state.shouldShowPreviousInsurer)
        assertFalse(state.shouldShowTpInsurer)
        assertFalse(state.shouldShowClaimAndNcb)

        // Incomplete specs -> invalid
        assertFalse(state.isFormValid)

        // Complete specs without any rollover -> valid
        val validState = state.copy(
            customerName = "Rahul Sharma",
            selectedRto = DropdownOption("MH-01", "MH01 - Mumbai"),
            selectedModel = DropdownOption("HONDA_AMAZE", "Honda Amaze"),
            selectedVariant = DropdownOption("1.2_S_CVT", "1.2 S CVT (1198CC)"),
            selectedFuel = DropdownOption("PETROL", "Petrol")
        )
        assertTrue(validState.isFormValid)
    }

    @Test
    fun `openCarDetailsSheet with unpopulated vehicle opens at Stage 1 MAKE_MODEL`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(entryMode = CarEntryMode.WITHOUT_CAR_NUMBER)

        vm.onAction(NewCarAction.OpenCarDetailsSheet)

        val state = vm.uiState.value
        assertTrue(state.isCarDetailsSheetOpen)
        assertEquals(CarDetailsStep.MAKE_MODEL, state.sheetStep)
        assertEquals(NavigationDirection.FORWARD, state.sheetNavigationDirection)
        assertNull(state.draftBrandModel)
        assertNull(state.draftFuel)
        assertNull(state.draftVariant)
        assertEquals("", state.sheetSearchQuery)
    }

    @Test
    fun `openCarDetailsSheet with prefilled vehicle starts at step 1 with current car highlighted and strict stepper`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        testDispatcher.scheduler.advanceUntilIdle()

        vm.onAction(NewCarAction.OpenCarDetailsSheet)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state.isCarDetailsSheetOpen)
        assertEquals(CarDetailsStep.MAKE_MODEL, state.sheetStep)
        assertNull(state.draftBrandModel)
        assertEquals("SELTOS", state.highlightedBrandModel?.modelId)
        assertEquals("DIESEL", state.highlightedFuel)
        assertEquals("SELTOS_PLUS_1.5_GTX_AT", state.highlightedVariant?.variantId)

        // Strict 1 -> 2 -> 3: steps 2 and 3 stay locked until chosen in this sheet
        vm.onAction(NewCarAction.OnSheetStepClick(CarDetailsStep.FUEL))
        vm.onAction(NewCarAction.OnSheetStepClick(CarDetailsStep.VARIANT))
        assertEquals(CarDetailsStep.MAKE_MODEL, vm.uiState.value.sheetStep)

        vm.onAction(NewCarAction.OnDraftBrandModelSelected(state.highlightedBrandModel!!))
        assertEquals(CarDetailsStep.FUEL, vm.uiState.value.sheetStep)
        vm.onAction(NewCarAction.OnSheetStepClick(CarDetailsStep.VARIANT))
        assertEquals(CarDetailsStep.FUEL, vm.uiState.value.sheetStep)
    }

    @Test
    fun `selecting brandModel cascades draft reset and advances forward to FUEL stage`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(entryMode = CarEntryMode.WITHOUT_CAR_NUMBER)
        vm.onAction(NewCarAction.OpenCarDetailsSheet)

        val creta = CarBrandModelItem(
            makeId = "HYUNDAI",
            makeName = "HYUNDAI",
            modelId = "CRETA",
            modelName = "CRETA"
        )
        vm.onAction(NewCarAction.OnDraftBrandModelSelected(creta))

        val state = vm.uiState.value
        assertEquals(CarDetailsStep.FUEL, state.sheetStep)
        assertEquals(NavigationDirection.FORWARD, state.sheetNavigationDirection)
        assertEquals(creta, state.draftBrandModel)
        assertNull(state.draftFuel)
        assertNull(state.draftVariant)
        assertEquals("", state.sheetSearchQuery)
    }

    @Test
    fun `selecting fuel cascades draft reset and advances forward to VARIANT stage`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(entryMode = CarEntryMode.WITHOUT_CAR_NUMBER)
        vm.onAction(NewCarAction.OpenCarDetailsSheet)

        val creta = CarBrandModelItem("HYUNDAI", "HYUNDAI", "CRETA", "CRETA")
        vm.onAction(NewCarAction.OnDraftBrandModelSelected(creta))
        vm.onAction(NewCarAction.OnDraftFuelSelected("PETROL"))

        val state = vm.uiState.value
        assertEquals(CarDetailsStep.VARIANT, state.sheetStep)
        assertEquals(NavigationDirection.FORWARD, state.sheetNavigationDirection)
        assertEquals("PETROL", state.draftFuel)
        assertNull(state.draftVariant)
        assertEquals("", state.sheetSearchQuery)
    }

    @Test
    fun `selecting variant commits vehicle details atomically and closes sheet`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(entryMode = CarEntryMode.WITHOUT_CAR_NUMBER)
        vm.onAction(NewCarAction.OpenCarDetailsSheet)

        val creta = CarBrandModelItem("HYUNDAI", "HYUNDAI", "CRETA", "CRETA")
        vm.onAction(NewCarAction.OnDraftBrandModelSelected(creta))
        vm.onAction(NewCarAction.OnDraftFuelSelected("PETROL"))

        val variantItem = CarVariantItem("CRETA_1.4_SX_O_DCT_TURBO", "1.4 SX (O) DCT TURBO (1353CC)", "PETROL")
        vm.onAction(NewCarAction.OnDraftVariantSelected(variantItem))

        val state = vm.uiState.value
        assertFalse(state.isCarDetailsSheetOpen)
        assertNull(state.draftBrandModel)
        assertNull(state.draftFuel)
        assertNull(state.draftVariant)

        // Verify committed state
        assertEquals("HYUNDAI", state.make)
        assertEquals("HYUNDAI", state.makeId)
        assertEquals("CRETA", state.model)
        assertEquals("CRETA", state.modelId)
        assertEquals("PETROL", state.fuel)
        assertEquals("1.4 SX (O) DCT TURBO (1353CC)", state.variant)
        assertEquals("CRETA_1.4_SX_O_DCT_TURBO", state.variantId)
        assertEquals("HYUNDAI-CRETA-1.4 SX (O) DCT TURBO (1353CC)-PETROL", state.formattedCarDetails)
        assertEquals("CRETA", state.selectedModel?.value)
        assertEquals("CRETA_1.4_SX_O_DCT_TURBO", state.selectedVariant?.value)
    }

    @Test
    fun `dismissing sheet via DismissCarDetailsSheet discards draft without overwriting committed vehicle`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        testDispatcher.scheduler.advanceUntilIdle()

        // Originally KIA SELTOS (vahan summary format)
        val originalVehicle = vm.uiState.value.formattedCarDetails
        assertEquals("KIA-SELTOS-Plus 1.5 GTX AT (1493CC)-DIESEL- (1493 CC)", originalVehicle)

        vm.onAction(NewCarAction.OpenCarDetailsSheet)

        // Select MARUTI SWIFT in draft
        val swift = CarBrandModelItem("MARUTI", "MARUTI", "SWIFT", "SWIFT")
        vm.onAction(NewCarAction.OnDraftBrandModelSelected(swift))
        vm.onAction(NewCarAction.OnDraftFuelSelected("PETROL"))

        // User taps ✕ to dismiss sheet mid-way
        vm.onAction(NewCarAction.DismissCarDetailsSheet)

        val state = vm.uiState.value
        assertFalse(state.isCarDetailsSheetOpen)
        assertNull(state.draftBrandModel)
        assertNull(state.draftFuel)
        assertNull(state.draftVariant)

        // Committed vehicle is untouched!
        assertEquals("KIA", state.make)
        assertEquals("SELTOS", state.model)
        assertEquals("DIESEL", state.fuel)
        assertEquals(originalVehicle, state.formattedCarDetails)
    }

    @Test
    fun `sheet back navigation steps backward from VARIANT to FUEL to MAKE_MODEL`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(entryMode = CarEntryMode.WITHOUT_CAR_NUMBER)
        vm.onAction(NewCarAction.OpenCarDetailsSheet)

        val creta = CarBrandModelItem("HYUNDAI", "HYUNDAI", "CRETA", "CRETA")
        vm.onAction(NewCarAction.OnDraftBrandModelSelected(creta))
        vm.onAction(NewCarAction.OnDraftFuelSelected("PETROL"))
        assertEquals(CarDetailsStep.VARIANT, vm.uiState.value.sheetStep)

        // Step back from VARIANT -> FUEL
        vm.onAction(NewCarAction.OnSheetBackClick)
        assertEquals(CarDetailsStep.FUEL, vm.uiState.value.sheetStep)
        assertEquals(NavigationDirection.BACKWARD, vm.uiState.value.sheetNavigationDirection)

        // Step back from FUEL -> MAKE_MODEL
        vm.onAction(NewCarAction.OnSheetBackClick)
        assertEquals(CarDetailsStep.MAKE_MODEL, vm.uiState.value.sheetStep)
        assertEquals(NavigationDirection.BACKWARD, vm.uiState.value.sheetNavigationDirection)

        // Back from MAKE_MODEL dismisses sheet
        vm.onAction(NewCarAction.OnSheetBackClick)
        assertFalse(vm.uiState.value.isCarDetailsSheetOpen)
    }

    @Test
    fun `search query filtering filters brandModels, fuelTypes, and variants`() {
        val liveModels = listOf(
            com.policyboss.policybosspro.feature.carInsurance.model.VehicleModel("101", "HYUNDAI", "CRETA", "HYUNDAI CRETA"),
            com.policyboss.policybosspro.feature.carInsurance.model.VehicleModel("102", "MARUTI", "SWIFT", "MARUTI SWIFT")
        )
        val vm = NewCarViewModel(object : FakeCarInsuranceRepository(models = { Resource.Success(liveModels) }) {})
        vm.initForMode(entryMode = CarEntryMode.WITHOUT_CAR_NUMBER)
        testDispatcher.scheduler.advanceUntilIdle()
        vm.onAction(NewCarAction.OpenCarDetailsSheet)
        testDispatcher.scheduler.advanceUntilIdle()

        // Search brand models
        vm.onAction(NewCarAction.OnSheetSearchQueryChange("creta"))
        val filteredBrands = vm.uiState.value.filteredBrandModels
        assertTrue(filteredBrands.all { it.displayName.lowercase().contains("creta") })
        assertEquals(1, filteredBrands.size)

        // Advance to fuel stage
        vm.onAction(NewCarAction.OnDraftBrandModelSelected(filteredBrands.first()))
        testDispatcher.scheduler.advanceUntilIdle() // beta_GetFuelVariant
        vm.onAction(NewCarAction.OnSheetSearchQueryChange("cng"))
        val filteredFuels = vm.uiState.value.filteredFuelTypes
        assertTrue(filteredFuels.all { it.lowercase().contains("cng") })

        // Advance to variant stage
        vm.onAction(NewCarAction.OnDraftFuelSelected(filteredFuels.first()))
        vm.onAction(NewCarAction.OnSheetSearchQueryChange("cng"))
        val filteredVariants = vm.uiState.value.filteredVariants
        assertTrue(filteredVariants.isNotEmpty())
        assertTrue(filteredVariants.all { it.variantName.lowercase().contains("cng") })
    }

    @Test
    fun `with car number mode guards customer name, rto, mfg date, and reg date from changes`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(entryMode = CarEntryMode.WITH_CAR_NUMBER, carNumber = "RJ14-1234")
        testDispatcher.scheduler.advanceUntilIdle()

        val initialState = vm.uiState.value
        assertTrue(initialState.isContinueWithCarNumber)
        val originalCustomerName = initialState.customerName
        val originalRto = initialState.selectedRto
        val originalMfgMonth = initialState.manufacturingMonth
        val originalMfgYear = initialState.manufacturingYear
        val originalRegDate = initialState.registrationDate

        // Try changing customer name
        vm.onAction(NewCarAction.OnCustomerNameChange("Hacker Name"))
        assertEquals(originalCustomerName, vm.uiState.value.customerName)

        // Try changing RTO
        vm.onAction(NewCarAction.OnRtoSelected(DropdownOption("MH-01", "MH01 - Mumbai")))
        assertEquals(originalRto, vm.uiState.value.selectedRto)

        // Try changing manufacturing month & year
        vm.onAction(NewCarAction.UpdateManufacturingMonth("Dec"))
        vm.onAction(NewCarAction.UpdateManufacturingYear("1999"))
        assertEquals(originalMfgMonth, vm.uiState.value.manufacturingMonth)
        assertEquals(originalMfgYear, vm.uiState.value.manufacturingYear)

        // Try opening registration date picker
        vm.onAction(NewCarAction.OnOpenDatePicker(com.policyboss.policybosspro.feature.carInsurance.model.DatePickerType.REGISTRATION))
        assertNull(vm.uiState.value.activeDatePicker)

        // Policy expiry date picker is still allowed in WITH_CAR_NUMBER
        vm.onAction(NewCarAction.OnOpenDatePicker(com.policyboss.policybosspro.feature.carInsurance.model.DatePickerType.POLICY_EXPIRY))
        assertEquals(com.policyboss.policybosspro.feature.carInsurance.model.DatePickerType.POLICY_EXPIRY, vm.uiState.value.activeDatePicker)
    }

    @Test
    fun `without car number mode allows editing customer name, rto, mfg date, and reg date`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.initForMode(entryMode = CarEntryMode.WITHOUT_CAR_NUMBER)

        val initialState = vm.uiState.value
        assertFalse(initialState.isContinueWithCarNumber)

        // Change customer name
        vm.onAction(NewCarAction.OnCustomerNameChange("Rahul Sharma"))
        assertEquals("Rahul Sharma", vm.uiState.value.customerName)

        // Change RTO
        val newRto = DropdownOption("MH-02", "MH02 - Mumbai")
        vm.onAction(NewCarAction.OnRtoSelected(newRto))
        assertEquals(newRto, vm.uiState.value.selectedRto)

        // Change manufacturing month & year
        vm.onAction(NewCarAction.UpdateManufacturingMonth("Mar"))
        vm.onAction(NewCarAction.UpdateManufacturingYear("2023"))
        assertEquals("Mar", vm.uiState.value.manufacturingMonth)
        assertEquals("2023", vm.uiState.value.manufacturingYear)

        // Open registration date picker
        vm.onAction(NewCarAction.OnOpenDatePicker(com.policyboss.policybosspro.feature.carInsurance.model.DatePickerType.REGISTRATION))
        assertEquals(com.policyboss.policybosspro.feature.carInsurance.model.DatePickerType.REGISTRATION, vm.uiState.value.activeDatePicker)
    }
}
