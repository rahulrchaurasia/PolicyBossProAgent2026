package com.policyboss.policybosspro.feature.dashboard.model

sealed interface DashboardAction {
    data class SelectMainTab(val tab: String) : DashboardAction
    
    object ToggleDateRangeSelector : DashboardAction
    data class ApplyDateRange(val range: String) : DashboardAction
    
    data class SelectLeadsTab(val tab: String) : DashboardAction
    
    data class SelectBusinessCategory(val category: String) : DashboardAction
    data class SelectBusinessMetric(val metric: String) : DashboardAction
    
    object ViewMissedOpportunities : DashboardAction
}
