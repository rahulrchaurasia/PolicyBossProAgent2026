package com.policyboss.policybosspro.feature.home

import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.core.network.ConnectivityObserver
import com.policyboss.policybosspro.core.repository.home.HomeRepository
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.home.model.homeState.HomeAction
import com.policyboss.policybosspro.feature.home.model.homeState.HomeUiEvent
import com.policyboss.policybosspro.feature.home.model.homeState.HomeUiState
import com.policyboss.policybosspro.feature.home.util.VehicleRegistrationValidator
import com.policyboss.policybosspro.feature.home.viemodel.HomeViewModel
import com.policyboss.policybosspro.feature.login.data.model.UserConstantResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class HomeRegistrationValidationTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeConnectivityObserver = object : ConnectivityObserver {
        override val isConnected: Flow<Boolean> = flowOf(true)
        override fun isCurrentlyConnected(): Boolean = true
    }

    private fun createAppDataManager(): AppDataManager {
        val testFile = File.createTempFile("test_datastore_home_reg", ".preferences_pb")
        testFile.deleteOnExit()
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(testDispatcher + Job()),
            produceFile = { testFile }
        )
        return AppDataManager(testDataStore)
    }

    private val fakeHomeRepository = object : HomeRepository {
        override fun getEarningsSummary() = flowOf(Result.success(AppDummyData.earningsSummary))
        override fun getQuoteTabs() = flowOf(Result.success(AppDummyData.quoteVehicleTabs))
        override fun getPromoBanners() = flowOf(Result.success(AppDummyData.promoBanners))
        override fun getHomeMetrics() = flowOf(Result.success(AppDummyData.homeMetrics))
        override fun getTopLeads() = flowOf(Result.success(AppDummyData.topLeads))
        override fun getRenewalLeads() = flowOf(Result.success(AppDummyData.renewalLeads))
        override fun getQuickLinks() = flowOf(Result.success(AppDummyData.quickLinks))
        override fun getTrustBullets() = flowOf(Result.success(AppDummyData.trustBullets))
        override fun getPartnerLogos() = flowOf(Result.success(AppDummyData.partnerLogos))
        override suspend fun fetchAndSaveUserConstants(): Result<UserConstantResponse?> = Result.success(null)
        override suspend fun fetchAndSaveUserConstants(body: Map<String, String>) = null
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): HomeViewModel {
        return HomeViewModel(
            appDataManager = createAppDataManager(),
            homeRepository = fakeHomeRepository,
            connectivityObserver = fakeConnectivityObserver
        )
    }

    // =========================================================================
    // 1. DYNAMIC PLACEHOLDER TESTS (WITHOUT "REGISTER")
    // =========================================================================

    @Test
    fun `placeholder updates dynamically based on selected vehicle category`() {
        assertEquals("Enter car number", VehicleRegistrationValidator.getPlaceholder("Car"))
        assertEquals("Enter car number", VehicleRegistrationValidator.getPlaceholder("CAR"))

        assertEquals("Enter two wheeler number", VehicleRegistrationValidator.getPlaceholder("TW"))
        assertEquals("Enter two wheeler number", VehicleRegistrationValidator.getPlaceholder("Two Wheeler"))

        assertEquals("Enter commercial vehicle number", VehicleRegistrationValidator.getPlaceholder("CV"))
        assertEquals("Enter commercial vehicle number", VehicleRegistrationValidator.getPlaceholder("Commercial Vehicle"))
    }

    @Test
    fun `HomeUiState derives correct placeholder based on selectedQuoteTab`() {
        val carState = HomeUiState(selectedQuoteTab = "Car")
        assertEquals("Enter car number", carState.registrationPlaceholder)

        val twState = HomeUiState(selectedQuoteTab = "TW")
        assertEquals("Enter two wheeler number", twState.registrationPlaceholder)

        val cvState = HomeUiState(selectedQuoteTab = "CV")
        assertEquals("Enter commercial vehicle number", cvState.registrationPlaceholder)
    }

    // =========================================================================
    // 2. DYNAMIC KEYBOARD SWITCHING & SANITIZATION TESTS
    // =========================================================================

    @Test
    fun `dynamic keyboard switches type and capitalization based on character position`() {
        // Standard RTO Format (MH02AB1234)
        assertEquals(KeyboardType.Ascii, VehicleRegistrationValidator.resolveKeyboardType("", allowBhSeries = false))
        assertEquals(KeyboardType.Ascii, VehicleRegistrationValidator.resolveKeyboardType("M", allowBhSeries = false))
        assertEquals(KeyboardType.Number, VehicleRegistrationValidator.resolveKeyboardType("MH", allowBhSeries = false))
        assertEquals(KeyboardType.Number, VehicleRegistrationValidator.resolveKeyboardType("MH0", allowBhSeries = false))
        assertEquals(KeyboardType.Ascii, VehicleRegistrationValidator.resolveKeyboardType("MH02", allowBhSeries = false))
        assertEquals(KeyboardType.Ascii, VehicleRegistrationValidator.resolveKeyboardType("MH02A", allowBhSeries = false))
        assertEquals(KeyboardType.Number, VehicleRegistrationValidator.resolveKeyboardType("DL01S5", allowBhSeries = false))
        assertEquals(KeyboardType.Number, VehicleRegistrationValidator.resolveKeyboardType("MH02AB1", allowBhSeries = false))

        // Capitalization
        assertEquals(KeyboardCapitalization.Characters, VehicleRegistrationValidator.resolveCapitalization(KeyboardType.Ascii))
        assertEquals(KeyboardCapitalization.None, VehicleRegistrationValidator.resolveCapitalization(KeyboardType.Number))

        // BH Series (22BH1234AA)
        assertEquals(KeyboardType.Number, VehicleRegistrationValidator.resolveKeyboardType("2", allowBhSeries = true))
        assertEquals(KeyboardType.Ascii, VehicleRegistrationValidator.resolveKeyboardType("22", allowBhSeries = true))
        assertEquals(KeyboardType.Ascii, VehicleRegistrationValidator.resolveKeyboardType("22B", allowBhSeries = true))
        assertEquals(KeyboardType.Number, VehicleRegistrationValidator.resolveKeyboardType("22BH", allowBhSeries = true))
        assertEquals(KeyboardType.Number, VehicleRegistrationValidator.resolveKeyboardType("22BH123", allowBhSeries = true))
        assertEquals(KeyboardType.Ascii, VehicleRegistrationValidator.resolveKeyboardType("22BH1234", allowBhSeries = true))
    }

    @Test
    fun `filterVehicleRegistration normalizes uppercase, strips hyphens, and enforces sequential typing`() {
        // Standard Car: RJ14CV0002
        val raw = "rj-14-cv-0002"
        assertEquals("RJ14CV0002", VehicleRegistrationValidator.filterVehicleRegistration(raw, allowBhSeries = true))

        // Rejects letters in RTO digits: MHAB1234 -> MH
        assertEquals("MH", VehicleRegistrationValidator.filterVehicleRegistration("MHAB", allowBhSeries = false))

        // Single series letter: DL01S5678
        assertEquals("DL01S5678", VehicleRegistrationValidator.filterVehicleRegistration("dl-01-s-5678", allowBhSeries = false))

        // BH series: 22BH1234AA
        assertEquals("22BH1234AA", VehicleRegistrationValidator.filterVehicleRegistration("22-bh-1234-aa", allowBhSeries = true))

        // When BH series not allowed, leading digits are rejected
        assertEquals("", VehicleRegistrationValidator.filterVehicleRegistration("22", allowBhSeries = false))
        assertEquals("BH", VehicleRegistrationValidator.filterVehicleRegistration("22BH", allowBhSeries = false))
    }

    // =========================================================================
    // 3. VEHICLE REGISTRATION VALIDATION TESTS
    // =========================================================================

    @Test
    fun `Car registration numbers accept valid Indian registration patterns`() {
        // Standard Car
        assertTrue(VehicleRegistrationValidator.isValid("Car", "RJ14CV0002"))
        assertTrue(VehicleRegistrationValidator.isValid("Car", "RJ-14-CV-0002"))
        assertTrue(VehicleRegistrationValidator.isValid("Car", "MH02AB1234"))
        assertTrue(VehicleRegistrationValidator.isValid("Car", "DL01S5678"))
        assertTrue(VehicleRegistrationValidator.isValid("Car", "22BH1234AA"))

        // Invalid Car inputs
        assertFalse(VehicleRegistrationValidator.isValid("Car", "1234"))
        assertFalse(VehicleRegistrationValidator.isValid("Car", "ABCD"))
        assertFalse(VehicleRegistrationValidator.isValid("Car", "RJ14"))
        assertFalse(VehicleRegistrationValidator.isValid("Car", ""))
    }

    @Test
    fun `Two Wheeler registration numbers accept SS-RR-LL-NNNN pattern`() {
        // Two Wheeler format SS-RR-LL-NNNN
        assertTrue(VehicleRegistrationValidator.isValid("TW", "MH02AB1234"))
        assertTrue(VehicleRegistrationValidator.isValid("TW", "MH-02-AB-1234"))
        assertTrue(VehicleRegistrationValidator.isValid("TW", "DL01S5678"))
        assertTrue(VehicleRegistrationValidator.isValid("TW", "KA05EV9999"))

        // Invalid Two Wheeler inputs
        assertFalse(VehicleRegistrationValidator.isValid("TW", "1234"))
        assertFalse(VehicleRegistrationValidator.isValid("TW", "MH021234")) // Missing LL series
        assertFalse(VehicleRegistrationValidator.isValid("TW", ""))
    }

    @Test
    fun `Commercial Vehicle registration preserves existing validation behavior`() {
        assertTrue(VehicleRegistrationValidator.isValid("CV", "MH12Q1234"))
        assertTrue(VehicleRegistrationValidator.isValid("CV", "NL01A1234"))
        assertFalse(VehicleRegistrationValidator.isValid("CV", ""))
    }

    // =========================================================================
    // 4. VIEWMODEL ACTION & TAB SWITCHING TESTS
    // =========================================================================

    @Test
    fun `switching tabs immediately updates selectedQuoteTab and derived placeholder`() = runTest {
        val vm = createViewModel()
        assertEquals("Car", vm.uiState.value.selectedQuoteTab)
        assertEquals("Enter car number", vm.uiState.value.registrationPlaceholder)

        vm.onAction(HomeAction.OnQuoteTabSelected("TW"))
        assertEquals("TW", vm.uiState.value.selectedQuoteTab)
        assertEquals("Enter two wheeler number", vm.uiState.value.registrationPlaceholder)

        vm.onAction(HomeAction.OnQuoteTabSelected("CV"))
        assertEquals("CV", vm.uiState.value.selectedQuoteTab)
        assertEquals("Enter commercial vehicle number", vm.uiState.value.registrationPlaceholder)
    }

    @Test
    fun `entering registration number updates state and clears previous error`() = runTest {
        val vm = createViewModel()

        vm.onAction(HomeAction.OnRegistrationNumberChange("rj14cv0002"))
        assertEquals("RJ14CV0002", vm.uiState.value.registrationNumber)
        assertNull(vm.uiState.value.registrationError)
    }

    // =========================================================================
    // 5. GET QUOTES CTA NAVIGATION & VALIDATION BEHAVIOR TESTS
    // =========================================================================

    @Test
    fun `valid Car registration emits NavigateToCarQuotes event`() = runTest {
        val vm = createViewModel()
        val events = mutableListOf<HomeUiEvent>()
        val job = launch { vm.uiEvent.collect { events.add(it) } }

        vm.onAction(HomeAction.OnQuoteTabSelected("Car"))
        vm.onAction(HomeAction.OnRegistrationNumberChange("RJ14CV0002"))
        vm.onAction(HomeAction.OnGetQuotesClick(regNo = "RJ14CV0002", tab = "Car"))
        advanceUntilIdle()

        assertTrue(events.any { it is HomeUiEvent.NavigateToCarQuotes && it.registrationNumber == "RJ14CV0002" })
        assertNull(vm.uiState.value.registrationError)

        job.cancel()
    }

    @Test
    fun `invalid Car registration displays feedback and does not navigate`() = runTest {
        val vm = createViewModel()
        val events = mutableListOf<HomeUiEvent>()
        val job = launch { vm.uiEvent.collect { events.add(it) } }

        vm.onAction(HomeAction.OnQuoteTabSelected("Car"))
        vm.onAction(HomeAction.OnRegistrationNumberChange("1234"))
        vm.onAction(HomeAction.OnGetQuotesClick(regNo = "1234", tab = "Car"))
        advanceUntilIdle()

        assertFalse(events.any { it is HomeUiEvent.NavigateToCarQuotes })
        assertTrue(events.any { it is HomeUiEvent.ShowSnackbar && it.message.contains("valid car number") })
        assertEquals("Please enter a valid car number (e.g. RJ14CV0002 or 22BH1234AA)", vm.uiState.value.registrationError)

        job.cancel()
    }

    @Test
    fun `valid TW registration does not navigate to Car Insurance`() = runTest {
        val vm = createViewModel()
        val events = mutableListOf<HomeUiEvent>()
        val job = launch { vm.uiEvent.collect { events.add(it) } }

        vm.onAction(HomeAction.OnQuoteTabSelected("TW"))
        vm.onAction(HomeAction.OnRegistrationNumberChange("MH02AB1234"))
        vm.onAction(HomeAction.OnGetQuotesClick(regNo = "MH02AB1234", tab = "TW"))
        advanceUntilIdle()

        assertFalse(events.any { it is HomeUiEvent.NavigateToCarQuotes })
        assertTrue(events.any { it is HomeUiEvent.ShowSnackbar && it.message.contains("Two-wheeler") })
        assertNull(vm.uiState.value.registrationError)

        job.cancel()
    }

    @Test
    fun `CV registration preserves existing behavior and does not navigate to Car Insurance`() = runTest {
        val vm = createViewModel()
        val events = mutableListOf<HomeUiEvent>()
        val job = launch { vm.uiEvent.collect { events.add(it) } }

        vm.onAction(HomeAction.OnQuoteTabSelected("CV"))
        vm.onAction(HomeAction.OnRegistrationNumberChange("MH12Q1234"))
        vm.onAction(HomeAction.OnGetQuotesClick(regNo = "MH12Q1234", tab = "CV"))
        advanceUntilIdle()

        assertFalse(events.any { it is HomeUiEvent.NavigateToCarQuotes })
        assertTrue(events.any { it is HomeUiEvent.ShowSnackbar && it.message.contains("Fetching quotes for MH12Q1234 (CV)") })
        assertNull(vm.uiState.value.registrationError)

        job.cancel()
    }

    @Test
    fun `blank registration number displays vehicle-specific empty error feedback`() = runTest {
        val vm = createViewModel()
        val events = mutableListOf<HomeUiEvent>()
        val job = launch { vm.uiEvent.collect { events.add(it) } }

        // Car empty
        vm.onAction(HomeAction.OnQuoteTabSelected("Car"))
        vm.onAction(HomeAction.OnRegistrationNumberChange(""))
        vm.onAction(HomeAction.OnGetQuotesClick(regNo = "", tab = "Car"))
        advanceUntilIdle()

        assertTrue(events.any { it is HomeUiEvent.ShowSnackbar && it.message == "Please enter car number" })
        assertEquals("Please enter car number", vm.uiState.value.registrationError)

        // TW empty
        vm.onAction(HomeAction.OnQuoteTabSelected("TW"))
        vm.onAction(HomeAction.OnGetQuotesClick(regNo = "", tab = "TW"))
        advanceUntilIdle()

        assertTrue(events.any { it is HomeUiEvent.ShowSnackbar && it.message == "Please enter two wheeler number" })
        assertEquals("Please enter two wheeler number", vm.uiState.value.registrationError)

        job.cancel()
    }
}
