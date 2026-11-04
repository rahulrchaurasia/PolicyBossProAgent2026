package com.policyboss.policybosspro.core.repository.sales

import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.sales.model.SalePolicy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalesRepositoryImpl @Inject constructor() : SalesRepository {

    override fun getSales(): Flow<Result<List<SalePolicy>>> = flow {
        emit(Result.success(AppDummyData.salesPolicies))
    }

    override fun getPolicyById(policyId: String): Flow<Result<SalePolicy>> = flow {
        val policy = AppDummyData.salesPolicies.find { it.id == policyId }
        if (policy != null) {
            emit(Result.success(policy))
        } else {
            emit(Result.failure(NoSuchElementException("Policy with id $policyId not found")))
        }
    }

    override fun getFilterProducts(): Flow<List<String>> = flow {
        emit(AppDummyData.filterProducts)
    }

    override fun getFilterSubUsers(): Flow<List<String>> = flow {
        emit(AppDummyData.filterSubUsers)
    }

    override fun getFilterSubPosps(): Flow<List<String>> = flow {
        emit(AppDummyData.filterSubPosps)
    }
}
