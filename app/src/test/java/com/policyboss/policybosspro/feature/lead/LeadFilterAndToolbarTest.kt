package com.policyboss.policybosspro.feature.lead

import com.policyboss.policybosspro.core.network.ConnectivityObserver
import com.policyboss.policybosspro.core.repository.lead.LeadRepository
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.home.model.home.HomeLead
import com.policyboss.policybosspro.feature.lead.model.FilterCategory
import com.policyboss.policybosspro.feature.lead.model.FilterCriteria
import com.policyboss.policybosspro.feature.lead.model.LeadAction
import com.policyboss.policybosspro.feature.lead.model.LeadSortOption
import com.policyboss.policybosspro.feature.lead.model.LeadUiEvent
import com.policyboss.policybosspro.feature.lead.model.LeadUiState
import com.policyboss.policybosspro.feature.lead.ui.LeadViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LeadFilterAndToolbarTest {

    private val testDispatcher = StandardTestDispatcher()

    private val sampleLeads = AppDummyData.topLeads.mapIndexed { index, lead ->
        when (index) {
            0 -> lead.copy(relationCategory = "Family")
            1, 2 -> lead.copy(relationCategory = "Friends")
            else -> lead.copy(relationCategory = "Random/Others")
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

    private fun createViewModel(
        leads: List<HomeLead> = sampleLeads,
        initiallyOnline: Boolean = true
    ): Pair<LeadViewModel, MutableStateFlow<Boolean>> {
        val connectivityFlow = MutableStateFlow(initiallyOnline)
        val fakeObserver = object : ConnectivityObserver {
            override val isConnected: Flow<Boolean> = connectivityFlow
            override fun isCurrentlyConnected(): Boolean = connectivityFlow.value
        }
        val fakeRepo = object : LeadRepository {
            override fun getLeads(): Flow<Result<List<HomeLead>>> = flowOf(Result.success(leads))
            override fun getLeadById(id: String) = flowOf(Result.success(leads.first()))
            override suspend fun updateLead(lead: HomeLead) = Result.success(lead)
            override suspend fun markLeadAsLost(leadId: String, reason: String, otherDescription: String?) = Result.success(Unit)
            override suspend fun markVehicleAsSold(leadId: String) = Result.success(Unit)
            override suspend fun saveRelationship(leadId: String, category: String, subCategory: String) = Result.success(Unit)
        }
        return LeadViewModel(fakeRepo, fakeObserver) to connectivityFlow
    }

    // =========================================================================
    // 1. TAB & SEARCH FILTERING
    // =========================================================================

    @Test
    fun `tabs filter Renewals, Synced contacts, and Added by me accurately`() {
        val base = LeadUiState(allLeads = sampleLeads)
        assertEquals(9, base.filteredLeads.size)

        val renewals = base.copy(selectedTab = "Renewals").filteredLeads
        assertTrue(renewals.isNotEmpty())
        assertTrue(renewals.all { it.leadType.equals("Renewal", ignoreCase = true) })

        val synced = base.copy(selectedTab = "Synced contacts").filteredLeads
        assertTrue(synced.isNotEmpty())
        assertTrue(synced.all { it.leadType.equals("Sync", ignoreCase = true) })

        val addedByMe = base.copy(selectedTab = "Added by me").filteredLeads
        assertTrue(addedByMe.isNotEmpty())
        assertTrue(addedByMe.all { it.leadType.equals("Added by me", ignoreCase = true) })
    }

    @Test
    fun `search filters across lead name, vehicle model, and registration number and resets on close`() = runTest {
        val (vm, _) = createViewModel()
        advanceUntilIdle()

        vm.onAction(LeadAction.ToggleSearchMode(true))
        assertTrue(vm.uiState.value.isSearchActive)

        // By lead name
        vm.onAction(LeadAction.UpdateSearchQuery("Nida"))
        assertEquals(listOf("lead_1"), vm.uiState.value.filteredLeads.map { it.id })

        // By vehicle model
        vm.onAction(LeadAction.UpdateSearchQuery("Activa"))
        assertEquals(listOf("lead_3"), vm.uiState.value.filteredLeads.map { it.id })

        // By registration number
        vm.onAction(LeadAction.UpdateSearchQuery("GJ-01-CV"))
        assertEquals(listOf("lead_5"), vm.uiState.value.filteredLeads.map { it.id })

        // Close resets query & restores full list
        vm.onAction(LeadAction.ToggleSearchMode(false))
        assertFalse(vm.uiState.value.isSearchActive)
        assertEquals("", vm.uiState.value.searchQuery)
        assertEquals(9, vm.uiState.value.filteredLeads.size)
    }

    // =========================================================================
    // 2. MULTI-CATEGORY FILTER CRITERIA
    // =========================================================================

    @Test
    fun `stage, vehicle type, renewal time, earnings range, and relationship filters work accurately`() {
        // Stage: Fresh/New -> New, Ongoing -> In Progress
        val freshState = LeadUiState(
            allLeads = sampleLeads,
            filterCriteria = FilterCriteria(stages = setOf("Fresh/New"))
        )
        assertTrue(freshState.filteredLeads.isNotEmpty())
        assertTrue(freshState.filteredLeads.all { it.status == "New" })

        val ongoingState = LeadUiState(
            allLeads = sampleLeads,
            filterCriteria = FilterCriteria(stages = setOf("Ongoing"))
        )
        assertTrue(ongoingState.filteredLeads.isNotEmpty())
        assertTrue(ongoingState.filteredLeads.all { it.status == "In Progress" })

        // Vehicle Type: Two Wheeler, Commercial Vehicle
        val cvState = LeadUiState(
            allLeads = sampleLeads,
            filterCriteria = FilterCriteria(vehicleTypes = setOf("Commercial Vehicle"))
        )
        assertEquals(3, cvState.filteredLeads.size)
        assertTrue(cvState.filteredLeads.all { it.vehicleType == "Commercial Vehicle" })

        // Renewal Time: 7 days (<=7d), 7-14 days (8..14d), 14+ days (>14d)
        val within7 = LeadUiState(
            allLeads = sampleLeads,
            filterCriteria = FilterCriteria(renewalTimes = setOf("7 days"))
        )
        assertEquals(setOf("lead_1", "lead_2", "lead_3", "lead_9"), within7.filteredLeads.map { it.id }.toSet())

        val between7And14 = LeadUiState(
            allLeads = sampleLeads,
            filterCriteria = FilterCriteria(renewalTimes = setOf("7-14 days"))
        )
        assertEquals(setOf("lead_4", "lead_6", "lead_7"), between7And14.filteredLeads.map { it.id }.toSet())

        val over14 = LeadUiState(
            allLeads = sampleLeads,
            filterCriteria = FilterCriteria(renewalTimes = setOf("14+ days"))
        )
        assertEquals(setOf("lead_5", "lead_8"), over14.filteredLeads.map { it.id }.toSet())

        // Earnings range: 2600..3500 matches ₹2,800, ₹3,200, ₹3,000
        val earningsFiltered = LeadUiState(
            allLeads = sampleLeads,
            filterCriteria = FilterCriteria(earningsRange = 2600f..3500f)
        )
        assertEquals(setOf("lead_2", "lead_5", "lead_6"), earningsFiltered.filteredLeads.map { it.id }.toSet())
        assertEquals(1, earningsFiltered.filterCriteria.totalActiveFiltersCount)

        // Relationship: Family (1)
        val familyState = LeadUiState(
            allLeads = sampleLeads,
            filterCriteria = FilterCriteria(relationships = setOf("Family (1)"))
        )
        assertEquals(listOf("lead_1"), familyState.filteredLeads.map { it.id })
    }

    // =========================================================================
    // 3. SORT OPTIONS & TOOLBAR STATE FLOW
    // =========================================================================

    @Test
    fun `sort options order leads correctly`() {
        // HIGHEST_EARNINGS: maxBrokerage ₹4,500 first (lead_9), ₹550 last (lead_7)
        val byEarnings = LeadUiState(
            allLeads = sampleLeads,
            filterCriteria = FilterCriteria(sortBy = LeadSortOption.HIGHEST_EARNINGS)
        ).filteredLeads
        assertEquals("lead_9", byEarnings.first().id)
        assertEquals("lead_7", byEarnings.last().id)

        // LOWEST_PREMIUM: minPremium ₹1,600 first (lead_7), ₹14,500 last (lead_9)
        val byLowestPremium = LeadUiState(
            allLeads = sampleLeads,
            filterCriteria = FilterCriteria(sortBy = LeadSortOption.LOWEST_PREMIUM)
        ).filteredLeads
        assertEquals("lead_7", byLowestPremium.first().id)
        assertEquals("lead_9", byLowestPremium.last().id)

        // UPCOMING_RENEWAL: 2d first (lead_9), 25d last (lead_8)
        val byUpcomingRenewal = LeadUiState(
            allLeads = sampleLeads,
            filterCriteria = FilterCriteria(sortBy = LeadSortOption.UPCOMING_RENEWAL)
        ).filteredLeads
        assertEquals("lead_9", byUpcomingRenewal.first().id)
        assertEquals("lead_8", byUpcomingRenewal.last().id)

        // HIGHEST_DISCOUNT: specialDealsCount 4 first (lead_4)
        val byHighestDiscount = LeadUiState(
            allLeads = sampleLeads,
            filterCriteria = FilterCriteria(sortBy = LeadSortOption.HIGHEST_DISCOUNT)
        ).filteredLeads
        assertEquals("lead_4", byHighestDiscount.first().id)
    }

    @Test
    fun `filter dialog open, toggle, reset, dismiss, apply, and chip removal maintain clean state`() = runTest {
        val (vm, _) = createViewModel()
        advanceUntilIdle()

        // Open sheet & select category + filters
        vm.onAction(LeadAction.OnToggleFilterSheet)
        assertTrue(vm.uiState.value.showFilterSheet)

        vm.onAction(LeadAction.OnSelectFilterCategory(FilterCategory.VEHICLE_TYPE))
        assertEquals(FilterCategory.VEHICLE_TYPE, vm.uiState.value.activeFilterCategory)

        vm.onAction(LeadAction.ToggleVehicleTypeFilter("Car"))
        vm.onAction(LeadAction.ToggleRenewalTimeFilter("7 days"))
        assertEquals(2, vm.uiState.value.tempFilterCriteria.totalActiveFiltersCount)
        assertEquals(0, vm.uiState.value.filterCriteria.totalActiveFiltersCount)

        // Dismiss without applying -> discards temp selections
        vm.onAction(LeadAction.OnDismissFilterSheet)
        assertFalse(vm.uiState.value.showFilterSheet)
        assertEquals(0, vm.uiState.value.filterCriteria.totalActiveFiltersCount)

        // Re-open, select, Reset -> clears pending selections without closing
        vm.onAction(LeadAction.OnToggleFilterSheet)
        vm.onAction(LeadAction.ToggleStageFilter("Won"))
        assertEquals(1, vm.uiState.value.tempFilterCriteria.totalActiveFiltersCount)
        vm.onAction(LeadAction.ResetFilters)
        assertTrue(vm.uiState.value.showFilterSheet)
        assertEquals(0, vm.uiState.value.tempFilterCriteria.totalActiveFiltersCount)

        // Select again and Apply
        vm.onAction(LeadAction.ToggleVehicleTypeFilter("Car"))
        vm.onAction(LeadAction.UpdateEarningsRange(2600f..5000f))
        vm.onAction(LeadAction.ApplyFilters)
        assertFalse(vm.uiState.value.showFilterSheet)
        assertEquals(2, vm.uiState.value.filterCriteria.totalActiveFiltersCount)

        // Remove chip individually
        vm.onAction(LeadAction.RemoveFilterChip("VEHICLE", "Car"))
        assertEquals(1, vm.uiState.value.filterCriteria.totalActiveFiltersCount)

        vm.onAction(LeadAction.RemoveFilterChip("EARNINGS", "₹2600 - ₹5000"))
        assertEquals(0, vm.uiState.value.filterCriteria.totalActiveFiltersCount)
    }

    @Test
    fun `sort dropdown updates list sorting immediately and add button emits snackbar`() = runTest {
        val (vm, _) = createViewModel()
        advanceUntilIdle()

        val events = mutableListOf<LeadUiEvent>()
        val job = launch { vm.uiEvent.collect { events.add(it) } }

        // Toolbar sort dropdown
        vm.onAction(LeadAction.ToggleSortDropdown(true))
        assertTrue(vm.uiState.value.showSortDropdown)

        vm.onAction(LeadAction.SelectSortOption(LeadSortOption.LOWEST_PREMIUM))
        assertFalse(vm.uiState.value.showSortDropdown)
        assertEquals(LeadSortOption.LOWEST_PREMIUM, vm.uiState.value.filterCriteria.sortBy)

        // Add Lead action
        vm.onAction(LeadAction.OnAddLeadClick)
        advanceUntilIdle()
        assertTrue(events.any { it is LeadUiEvent.ShowSnackbar })

        job.cancel()
    }
}

