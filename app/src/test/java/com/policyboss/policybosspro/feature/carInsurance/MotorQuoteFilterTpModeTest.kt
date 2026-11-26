package com.policyboss.policybosspro.feature.carInsurance

import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState
import com.policyboss.policybosspro.feature.carInsurance.model.PolicyType
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.FilterCategory
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.QuoteFilterState
import com.policyboss.policybosspro.feature.carInsurance.viewmodel.NewCarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** TP-only quote filter: Additional Cover alone, no rail, no accessories, scoped count and reset. */
@OptIn(ExperimentalCoroutinesApi::class)
class MotorQuoteFilterTpModeTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(testDispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(policyType: PolicyType): NewCarViewModel =
        NewCarViewModel(FakeCarInsuranceRepository()).apply {
            initForMode(entryMode = CarEntryMode.WITHOUT_CAR_NUMBER)
            onAction(NewCarAction.SelectPolicyType(policyType))
        }

    @Test
    fun `TP shows only Additional Cover, without rail or accessories`() {
        val tp = NewCarUiState(policyType = PolicyType.TP_ONLY)
        assertEquals(listOf(FilterCategory.ADDITIONAL_COVER), tp.availableFilterCategories)
        assertFalse(tp.showFilterCategoryRail)
        assertFalse(tp.showAccessoriesFilter)
    }

    @Test
    fun `OD and Comprehensive keep every category, the rail and accessories`() {
        listOf(PolicyType.OD_ONLY, PolicyType.COMPREHENSIVE).forEach { type ->
            val state = NewCarUiState(policyType = type)
            assertEquals(FilterCategory.entries, state.availableFilterCategories)
            assertTrue(state.showFilterCategoryRail)
            assertTrue(state.showAccessoriesFilter)
        }
    }

    @Test
    fun `opening the filter in TP always lands on Additional Cover`() {
        val vm = viewModel(PolicyType.TP_ONLY)
        vm.onAction(NewCarAction.OpenFilterScreen(FilterCategory.SET_IDV))
        assertEquals(FilterCategory.ADDITIONAL_COVER, vm.uiState.value.selectedFilterCategory)

        vm.onAction(NewCarAction.OnSelectFilterCategory(FilterCategory.DISCOUNTS))
        assertEquals(FilterCategory.ADDITIONAL_COVER, vm.uiState.value.selectedFilterCategory)
    }

    @Test
    fun `opening the filter in OD keeps the requested category`() {
        val vm = viewModel(PolicyType.OD_ONLY)
        vm.onAction(NewCarAction.OpenFilterScreen(FilterCategory.SET_IDV))
        assertEquals(FilterCategory.SET_IDV, vm.uiState.value.selectedFilterCategory)
    }

    @Test
    fun `TP Apply count ignores hidden add-on and discount defaults`() {
        val filter = QuoteFilterState(
            selectedAddOnIds = setOf("zero_dep"),            // hidden in TP
            antiTheftDevice = true,                          // hidden in TP
            payAsYouDrive = true,                            // hidden in TP
            selectedLiabilityCoverIds = setOf("owner_driver_pa"),
            selectedUnNamedPassengerCover = "₹ 1,00,000"
        )
        assertEquals(2, NewCarUiState(policyType = PolicyType.TP_ONLY, draftQuoteFilter = filter).draftFilterCount)
        assertEquals(filter.activeFilterCount, NewCarUiState(policyType = PolicyType.COMPREHENSIVE, draftQuoteFilter = filter).draftFilterCount)
    }

    @Test
    fun `TP Reset clears only Additional Cover`() {
        val vm = viewModel(PolicyType.TP_ONLY)
        vm.onAction(NewCarAction.OpenFilterScreen(FilterCategory.ADDITIONAL_COVER))
        val before = vm.uiState.value.draftQuoteFilter
        vm.onAction(NewCarAction.OnSelectDraftUnNamedPassengerCover("₹ 50,000"))

        vm.onAction(NewCarAction.OnResetDraftFilters)

        val after = vm.uiState.value.draftQuoteFilter
        assertEquals(emptySet<String>(), after.selectedLiabilityCoverIds)
        assertEquals("No", after.selectedUnNamedPassengerCover)
        assertEquals(0, vm.uiState.value.draftFilterCount)
        // Hidden categories untouched
        assertEquals(before.selectedAddOnIds, after.selectedAddOnIds)
        assertEquals(before.antiTheftDevice, after.antiTheftDevice)
        assertEquals(before.selectedIdv, after.selectedIdv, 0f)
    }

    @Test
    fun `passenger cover keeps the original value, not the display label`() {
        val vm = viewModel(PolicyType.TP_ONLY)
        vm.onAction(NewCarAction.OnSelectDraftUnNamedPassengerCover("₹ 1,00,000"))
        assertEquals("₹ 1,00,000", vm.uiState.value.draftQuoteFilter.selectedUnNamedPassengerCover)
        assertFalse(vm.uiState.value.draftQuoteFilter.selectedUnNamedPassengerCover!!.contains(' '))
    }
}
