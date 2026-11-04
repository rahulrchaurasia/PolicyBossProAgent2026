package com.policyboss.policybosspro.core.repository.sales

import com.policyboss.policybosspro.feature.sales.model.SalePolicy
import kotlinx.coroutines.flow.Flow

interface SalesRepository {
    fun getSales(): Flow<Result<List<SalePolicy>>>
    fun getPolicyById(policyId: String): Flow<Result<SalePolicy>>
    fun getFilterProducts(): Flow<List<String>>
    fun getFilterSubUsers(): Flow<List<String>>
    fun getFilterSubPosps(): Flow<List<String>>
}
