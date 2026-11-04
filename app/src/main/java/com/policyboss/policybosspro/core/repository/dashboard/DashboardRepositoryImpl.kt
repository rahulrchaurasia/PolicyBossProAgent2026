package com.policyboss.policybosspro.core.repository.dashboard

import com.policyboss.policybosspro.feature.dashboard.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor() : DashboardRepository {
    
    override fun getEarnings(): Flow<Result<DashboardEarnings>> = flow {
        emit(Result.success(DashboardDummyData.earnings))
    }

    override fun getTotalPoliciesSold(): Flow<Result<List<PolicyCategoryCount>>> = flow {
        emit(Result.success(DashboardDummyData.totalPoliciesSold))
    }

    override fun getLeadsByStage(): Flow<Result<List<LeadStageCount>>> = flow {
        emit(Result.success(DashboardDummyData.leadsByStage))
    }

    override fun getLeadsByProduct(): Flow<Result<List<PolicyCategoryCount>>> = flow {
        emit(Result.success(DashboardDummyData.leadsByProduct))
    }

    override fun getMissedOpportunities(): Flow<Result<MissedOpportunities>> = flow {
        emit(Result.success(DashboardDummyData.missedOpportunities))
    }

    override fun getBusinessSnapshot(category: String, metric: String): Flow<Result<BusinessSnapshotData>> = flow {
        // Here we just return the dummy one, in reality filter by category/metric
        emit(Result.success(DashboardDummyData.businessSnapshot.copy(category = category)))
    }

    override fun getEarningsChart(): Flow<Result<List<EarningsChartPoint>>> = flow {
        emit(Result.success(DashboardDummyData.earningsChart))
    }
}
