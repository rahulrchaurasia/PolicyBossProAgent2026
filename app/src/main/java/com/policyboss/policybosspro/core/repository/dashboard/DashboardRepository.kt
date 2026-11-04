package com.policyboss.policybosspro.core.repository.dashboard

import com.policyboss.policybosspro.feature.dashboard.model.*
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun getEarnings(): Flow<Result<DashboardEarnings>>
    fun getTotalPoliciesSold(): Flow<Result<List<PolicyCategoryCount>>>
    fun getLeadsByStage(): Flow<Result<List<LeadStageCount>>>
    fun getLeadsByProduct(): Flow<Result<List<PolicyCategoryCount>>>
    fun getMissedOpportunities(): Flow<Result<MissedOpportunities>>
    fun getBusinessSnapshot(category: String, metric: String): Flow<Result<BusinessSnapshotData>>
    fun getEarningsChart(): Flow<Result<List<EarningsChartPoint>>>
}
