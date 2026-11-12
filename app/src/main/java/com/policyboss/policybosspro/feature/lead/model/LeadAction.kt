package com.policyboss.policybosspro.feature.lead.model

import com.policyboss.policybosspro.feature.home.model.home.HomeLead

sealed interface LeadAction {
    data class SelectLeadTab(val tab: String) : LeadAction
    data class UpdateSearchQuery(val query: String) : LeadAction
    data class ToggleSearchMode(val active: Boolean) : LeadAction

    // Toolbar & Filter Actions
    data object OnAddLeadClick : LeadAction
    data object OnToggleFilterSheet : LeadAction
    data object OnDismissFilterSheet : LeadAction
    data class OnSelectFilterCategory(val category: FilterCategory) : LeadAction
    data class ToggleStageFilter(val stage: String) : LeadAction
    data class ToggleVehicleTypeFilter(val vehicle: String) : LeadAction
    data class ToggleRenewalTimeFilter(val time: String) : LeadAction
    data class UpdateEarningsRange(val range: ClosedFloatingPointRange<Float>) : LeadAction
    data class ToggleRelationshipFilter(val relationship: String) : LeadAction
    data class SelectSortOption(val option: LeadSortOption) : LeadAction
    data object ApplyFilters : LeadAction
    data object ResetFilters : LeadAction
    data class RemoveFilterChip(val type: String, val value: String) : LeadAction
    data class ToggleSortDropdown(val show: Boolean) : LeadAction

    // Lead Details & Secondary Dialog Actions
    data class OpenLeadDetails(val lead: HomeLead) : LeadAction
    data object CloseLeadDetails : LeadAction

    data class OpenMarkAsLost(val lead: HomeLead) : LeadAction
    data object CloseMarkAsLost : LeadAction
    data class SubmitMarkAsLost(val reason: String, val otherDescription: String?) : LeadAction

    data class OpenMarkVehicleSold(val lead: HomeLead) : LeadAction
    data object CloseMarkVehicleSold : LeadAction
    data object SubmitMarkVehicleSold : LeadAction

    data class OpenAddRelationship(val lead: HomeLead) : LeadAction
    data object CloseAddRelationship : LeadAction
    data class SubmitRelationship(val category: String, val subCategory: String) : LeadAction

    data class OpenEditDetails(val lead: HomeLead) : LeadAction
    data object CloseEditDetails : LeadAction
    data class SubmitEditDetails(val ncbStatus: String, val expiryDate: String) : LeadAction

    data class ContinueToQuotes(val lead: HomeLead) : LeadAction
    data class ShareLead(val lead: HomeLead) : LeadAction

    data object LoadLeads : LeadAction
    data object OnRetryClick : LeadAction
}
