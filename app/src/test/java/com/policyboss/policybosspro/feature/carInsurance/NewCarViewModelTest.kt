package com.policyboss.policybosspro.feature.carInsurance

import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.feature.carInsurance.data.api.CarInsuranceApi
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteResponse
import com.policyboss.policybosspro.feature.carInsurance.data.repository.CarInsuranceRepository
import com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode
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

@OptIn(ExperimentalCoroutinesApi::class)
class NewCarViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val dummyApi = object : CarInsuranceApi {
        override suspend fun getCarQuotes(request: CarQuoteRequest): Response<CarQuoteResponse> {
            error("Fake repository overrides getCarQuotes directly")
        }
    }

    private fun fakeRepository(): CarInsuranceRepository = object : CarInsuranceRepository(dummyApi) {
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
    fun `tp only with existing policy shows expiry date but hides insurers and claims`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.init(isNewCar = false, carNumber = "RJ14-1234")

        // Switch to TP Only
        vm.onAction(NewCarAction.OnPolicyTypeSelected("TP Only"))
        vm.onAction(NewCarAction.OnExistingPolicyToggle(true))

        val state = vm.uiState.value
        assertEquals(PolicyType.TP_ONLY, state.policyType)
        assertEquals("Do You Have Existing Policy?", state.existingPolicyQuestionLabel)
        assertTrue(state.shouldShowExistingPolicySection)
        assertTrue(state.shouldShowExpiryDate)
        assertFalse(state.shouldShowPreviousInsurer)
        assertFalse(state.shouldShowTpInsurer)
        assertFalse(state.shouldShowClaimAndNcb)

        // Null previous insurer should NOT invalidate form in TP Only mode
        val stateWithoutInsurer = state.copy(selectedInsurer = null, policyExpiryDate = "29-Jan-2027")
        assertTrue(stateWithoutInsurer.isFormValid)

        // Missing expiry date DOES invalidate form
        val stateWithoutExpiry = state.copy(policyExpiryDate = "")
        assertFalse(stateWithoutExpiry.isFormValid)
    }

    @Test
    fun `tp only without existing policy hides all rollover fields`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.init(isNewCar = false, carNumber = "RJ14-1234")

        vm.onAction(NewCarAction.OnPolicyTypeSelected("TP Only"))
        vm.onAction(NewCarAction.OnExistingPolicyToggle(false))

        val state = vm.uiState.value
        assertTrue(state.shouldShowExistingPolicySection)
        assertFalse(state.shouldShowExpiryDate)
        assertFalse(state.shouldShowPreviousInsurer)
        assertFalse(state.shouldShowTpInsurer)
        assertFalse(state.shouldShowClaimAndNcb)
        assertTrue(state.isFormValid)
    }

    @Test
    fun `od only with existing policy shows expiry date, tp insurer, claim and ncb but hides previous insurer`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.init(isNewCar = false, carNumber = "RJ14-1234")

        // Switch to OD Only
        vm.onAction(NewCarAction.SelectPolicyType(PolicyType.OD_ONLY))
        vm.onAction(NewCarAction.OnExistingPolicyToggle(true))
        vm.onAction(NewCarAction.OnTpInsurerSelected(DropdownOption("ACKO", "Acko General Insurance")))

        val state = vm.uiState.value
        assertEquals(PolicyType.OD_ONLY, state.policyType)
        assertEquals("Do You Have Existing Comprehensive Policy?", state.existingPolicyQuestionLabel)
        assertTrue(state.shouldShowExistingPolicySection)
        assertTrue(state.shouldShowExpiryDate)
        assertFalse(state.shouldShowPreviousInsurer)
        assertTrue(state.shouldShowTpInsurer)
        assertTrue(state.shouldShowClaimAndNcb)

        // Missing Previous Insurer should NOT invalidate OD Only form
        val stateWithoutPrevInsurer = state.copy(
            selectedInsurer = null,
            selectedTpInsurer = DropdownOption("ACKO", "Acko General Insurance"),
            policyExpiryDate = "29-Jan-2027"
        )
        assertTrue(stateWithoutPrevInsurer.isFormValid)

        // Missing Policy TP Insurer DOES invalidate OD Only form
        val stateWithoutTpInsurer = stateWithoutPrevInsurer.copy(selectedTpInsurer = null)
        assertFalse(stateWithoutTpInsurer.isFormValid)
    }

    @Test
    fun `od only without existing policy hides all rollover fields`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.init(isNewCar = false, carNumber = "RJ14-1234")

        vm.onAction(NewCarAction.SelectPolicyType(PolicyType.OD_ONLY))
        vm.onAction(NewCarAction.OnExistingPolicyToggle(false))

        val state = vm.uiState.value
        assertFalse(state.shouldShowExpiryDate)
        assertFalse(state.shouldShowPreviousInsurer)
        assertFalse(state.shouldShowTpInsurer)
        assertFalse(state.shouldShowClaimAndNcb)
        assertTrue(state.isFormValid)
    }

    @Test
    fun `comprehensive with existing policy shows expiry date, previous insurer, claim and ncb but hides tp insurer`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.init(isNewCar = false, carNumber = "RJ14-1234")

        // Default mode is Comprehensive
        vm.onAction(NewCarAction.SelectPolicyType(PolicyType.COMPREHENSIVE))
        vm.onAction(NewCarAction.OnExistingPolicyToggle(true))

        val state = vm.uiState.value
        assertEquals(PolicyType.COMPREHENSIVE, state.policyType)
        assertEquals("Do You Have Existing Comprehensive Policy?", state.existingPolicyQuestionLabel)
        assertTrue(state.shouldShowExistingPolicySection)
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
        vm.init(isNewCar = false, carNumber = "RJ14-1234")

        vm.onAction(NewCarAction.SelectPolicyType(PolicyType.COMPREHENSIVE))
        vm.onAction(NewCarAction.OnExistingPolicyToggle(false))

        val state = vm.uiState.value
        assertFalse(state.shouldShowExpiryDate)
        assertFalse(state.shouldShowPreviousInsurer)
        assertFalse(state.shouldShowTpInsurer)
        assertFalse(state.shouldShowClaimAndNcb)
        assertTrue(state.isFormValid)
    }

    @Test
    fun `claim toggle to yes automatically locks ncb to zero percent`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.init(isNewCar = false, carNumber = "RJ14-1234")

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
    fun `new car without car number hides entire existing policy section`() {
        val vm = NewCarViewModel(fakeRepository())
        vm.init(isNewCar = true, carNumber = null)

        val state = vm.uiState.value
        assertEquals(CarEntryMode.WITHOUT_CAR_NUMBER, state.entryMode)
        assertTrue(state.isNewCar)
        assertFalse(state.shouldShowExistingPolicySection)
        assertFalse(state.shouldShowExpiryDate)
        assertFalse(state.shouldShowPreviousInsurer)
        assertFalse(state.shouldShowTpInsurer)
        assertFalse(state.shouldShowClaimAndNcb)
    }
}

