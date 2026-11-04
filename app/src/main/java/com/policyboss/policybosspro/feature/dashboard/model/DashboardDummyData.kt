package com.policyboss.policybosspro.feature.dashboard.model

import com.policyboss.policybosspro.R

object DashboardDummyData {
    val earnings = DashboardEarnings()

    val totalPoliciesSold = listOf(
        PolicyCategoryCount("Car", 50, R.drawable.ic_car),
        PolicyCategoryCount("TW", 100, R.drawable.ic_bike),
        PolicyCategoryCount("CV", 150, R.drawable.ic_cv),
        PolicyCategoryCount("Travel", 90, R.drawable.ic_travel),
        PolicyCategoryCount("Health", 150, R.drawable.ic_health),
        PolicyCategoryCount("Life", 90, R.drawable.ic_protect),
        PolicyCategoryCount("SME", 150, R.drawable.ic_sme) // Assuming sme icon
    )

    val leadsByStage = listOf(
        LeadStageCount("Fresh", 100),
        LeadStageCount("In Progress", 150),
        LeadStageCount("Closed", 50)
    )

    val leadsByProduct = totalPoliciesSold.map { it.copy() }

    val missedOpportunities = MissedOpportunities(
        breakdown = listOf(
            PolicyCategoryCount("Car", 15, R.drawable.ic_car),
            PolicyCategoryCount("TW", 5, R.drawable.ic_bike),
            PolicyCategoryCount("CV", 10, R.drawable.ic_cv),
            PolicyCategoryCount("Travel", 10, R.drawable.ic_travel)
        )
    )

    val businessSnapshot = BusinessSnapshotData(
        category = "Car",
        items = listOf(
            SnapshotItem("Bajaj Allianz", 21f, 21),
            SnapshotItem("Tata AIG", 18f, 18),
            SnapshotItem("Zuno", 15f, 15),
            SnapshotItem("Royal Sundaram", 13f, 13),
            SnapshotItem("Liberty General", 8f, 8),
            SnapshotItem("HDFC ERGO", 6f, 6),
            SnapshotItem("Shriram General", 4f, 4)
        ).sortedByDescending { it.value }
    )

    val earningsChart = listOf(
        EarningsChartPoint("Jan", 15000f),
        EarningsChartPoint("Feb", 20000f),
        EarningsChartPoint("Mar", 30000f),
        EarningsChartPoint("Apr", 18000f),
        EarningsChartPoint("May", 25000f),
        EarningsChartPoint("Jun", 10000f),
        EarningsChartPoint("Jul", 21500f)
    )
}