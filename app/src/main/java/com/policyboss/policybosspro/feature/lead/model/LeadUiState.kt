package com.policyboss.policybosspro.feature.lead.model

import com.policyboss.policybosspro.feature.home.model.home.HomeLead

data class LeadUiState(
    val isLoading: Boolean = false,
    val isOnline: Boolean = true,
    val isRefreshing: Boolean = false,
    val isInitialLoadCompleted: Boolean = false,
    val error: String? = null,
    val allLeads: List<HomeLead> = emptyList(),
    
    val selectedTab: String = "All Leads",
    val selectedStatusFilter: String? = null,
    val selectedExpiryFilter: String? = null,
    val selectedVehicleFilters: Set<String> = emptySet(),
    
    val selectedLead: HomeLead? = null,
    val activeDialog: LeadDialogType? = null
) {
    val hasCachedData: Boolean
        get() = allLeads.isNotEmpty()

    val isOffline: Boolean
        get() = !isOnline

    val showNoInternetScreen: Boolean
        get() = !isOnline && !hasCachedData && (error != null || !isInitialLoadCompleted) && !isLoading

    val filteredLeads: List<HomeLead>
        get() {
            var list = allLeads
            
            // 1. Tab Filter ("All Leads", "Renewals", "Synced contacts", "Added by me")
            if (selectedTab.equals("Renewals", ignoreCase = true)) {
                list = list.filter { it.leadType.equals("Renewal", ignoreCase = true) }
            } else if (selectedTab.equals("Synced contacts", ignoreCase = true)) {
                list = list.filter { it.leadType.equals("Sync", ignoreCase = true) }
            } else if (selectedTab.equals("Added by me", ignoreCase = true)) {
                list = list.filter { it.leadType.equals("Added by me", ignoreCase = true) || it.leadType.equals("Manual", ignoreCase = true) }
            }
            
            // 2. Status Filter ("New", "In Progress", "Won", "Lost")
            if (!selectedStatusFilter.isNullOrBlank()) {
                list = list.filter { it.status.equals(selectedStatusFilter, ignoreCase = true) }
            }

            // 3. Expiry Filter ("7 days", "7-14 days", "14+ days")
            if (selectedExpiryFilter == "7 days") {
                list = list.filter { it.expiryText.contains("1d") || it.expiryText.contains("2d") || it.expiryText.contains("3d") || it.expiryText.contains("7d") }
            } else if (selectedExpiryFilter == "7-14 days") {
                list = list.filter { it.expiryText.contains("8d") || it.expiryText.contains("9d") || it.expiryText.contains("10d") || it.expiryText.contains("12d") || it.expiryText.contains("14d") }
            } else if (selectedExpiryFilter == "14+ days") {
                list = list.filter { it.expiryText.contains("15d") || it.expiryText.contains("20d") || it.expiryText.contains("21d") || it.expiryText.contains("25d") || it.expiryText.contains("30d") }
            }
            
            // 4. Vehicle Filter ("Car", "Two Wheeler", "Commercial Vehicle")
            if (selectedVehicleFilters.isNotEmpty()) {
                list = list.filter { selectedVehicleFilters.contains(it.vehicleType) }
            }
            
            return list
        }
}

enum class LeadDialogType {
    MARK_LOST,
    MARK_SOLD,
    ADD_RELATIONSHIP,
    EDIT_DETAILS
}
