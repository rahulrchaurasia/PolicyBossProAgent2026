package com.policyboss.policybosspro.feature.dashboard.model

data class DashboardUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // Top tabs
    val selectedMainTab: String = "Performance",
    
    // Date Range
    val selectedDateRange: String = "1 Jul, 26 - 31 Jul, 26",
    val showDateRangeSelector: Boolean = false,

    // Earnings
    val earnings: DashboardEarnings = DashboardEarnings(),
    
    // Policies
    val totalPoliciesSold: List<PolicyCategoryCount> = emptyList(),
    
    // Leads
    val selectedLeadsTab: String = "By Stage", // "By Stage" or "By Product"
    val leadsByStage: List<LeadStageCount> = emptyList(),
    val leadsByProduct: List<PolicyCategoryCount> = emptyList(),
    
    // Missed Opportunities
    val missedOpportunities: MissedOpportunities = MissedOpportunities(),
    
    // Business Snapshot
    val businessCategories: List<String> = listOf("Car", "TW", "CV", "Travel", "Health", "Life"),
    val selectedBusinessCategory: String = "Car",
    val selectedBusinessMetric: String = "Count", // "Count" or "Premium"
    val businessSnapshot: BusinessSnapshotData = BusinessSnapshotData(category = "Car"),
    
    // Earnings Chart
    val earningsChart: List<EarningsChartPoint> = emptyList()
)
