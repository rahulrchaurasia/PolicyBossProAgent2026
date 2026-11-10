package com.policyboss.policybosspro.core.repository.home

import com.policyboss.policybosspro.feature.home.model.home.EarningsSummary
import com.policyboss.policybosspro.feature.home.model.home.HomeLead
import com.policyboss.policybosspro.feature.home.model.home.HomeMetric
import com.policyboss.policybosspro.feature.home.model.home.PromoBanner
import com.policyboss.policybosspro.feature.home.model.home.QuickLink
import com.policyboss.policybosspro.feature.home.model.home.TrustBulletPoint
import com.policyboss.policybosspro.feature.home.model.menu.QuoteTabItem
import com.policyboss.policybosspro.feature.login.data.model.UserConstantResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface HomeRepository {

    suspend fun fetchAndSaveUserConstants(): Result<UserConstantResponse?>
    suspend fun fetchAndSaveUserConstants(body: Map<String, String>): Response<UserConstantResponse>? = null
    fun getEarningsSummary(): Flow<Result<EarningsSummary>>
    fun getQuoteTabs(): Flow<Result<List<QuoteTabItem>>>
    fun getPromoBanners(): Flow<Result<List<PromoBanner>>>
    fun getHomeMetrics(): Flow<Result<List<HomeMetric>>>
    fun getTopLeads(): Flow<Result<List<HomeLead>>>
    fun getRenewalLeads(): Flow<Result<List<HomeLead>>>
    fun getQuickLinks(): Flow<Result<List<QuickLink>>>
    fun getTrustBullets(): Flow<Result<List<TrustBulletPoint>>>
    fun getPartnerLogos(): Flow<Result<List<Int>>>
}