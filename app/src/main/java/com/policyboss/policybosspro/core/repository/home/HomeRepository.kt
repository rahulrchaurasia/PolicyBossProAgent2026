package com.policyboss.policybosspro.core.repository.home

import com.policyboss.policybosspro.feature.home.model.home.EarningsSummary
import com.policyboss.policybosspro.feature.home.model.home.HomeLead
import com.policyboss.policybosspro.feature.home.model.home.HomeMetric
import com.policyboss.policybosspro.feature.home.model.home.PromoBanner
import com.policyboss.policybosspro.feature.home.model.home.QuickLink
import com.policyboss.policybosspro.feature.home.model.home.TrustBulletPoint
import com.policyboss.policybosspro.feature.home.model.menu.QuoteTabItem
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
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