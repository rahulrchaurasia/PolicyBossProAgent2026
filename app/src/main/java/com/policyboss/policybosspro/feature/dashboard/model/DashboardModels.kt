package com.policyboss.policybosspro.feature.dashboard.model

import androidx.compose.runtime.Immutable

@Immutable
data class DashboardEarnings(
    val amount: String = "₹21,500",
    val premium: String = "₹1,23,452",
    val potential: String = "₹1,20,403"
)

@Immutable
data class PolicyCategoryCount(
    val categoryName: String,
    val count: Int,
    val iconResId: Int? = null
)

@Immutable
data class LeadStageCount(
    val stage: String,
    val count: Int
)

@Immutable
data class MissedOpportunities(
    val brokerageLost: String = "₹30,00",
    val policiesLost: Int = 234,
    val breakdown: List<PolicyCategoryCount> = emptyList()
)

@Immutable
data class BusinessSnapshotData(
    val category: String,
    val items: List<SnapshotItem> = emptyList(),
    val conversionPercentage: Int = 30
)

@Immutable
data class SnapshotItem(
    val name: String,
    val value: Float,
    val count: Int? = null
)

@Immutable
data class EarningsChartPoint(
    val month: String,
    val earnings: Float
)
