package com.policyboss.policybosspro.core.repository.home

import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.home.model.home.EarningsSummary
import com.policyboss.policybosspro.feature.home.model.home.HomeLead
import com.policyboss.policybosspro.feature.home.model.home.HomeMetric
import com.policyboss.policybosspro.feature.home.model.home.PromoBanner
import com.policyboss.policybosspro.feature.home.model.home.QuickLink
import com.policyboss.policybosspro.feature.home.model.home.TrustBulletPoint
import com.policyboss.policybosspro.feature.home.model.menu.QuoteTabItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor() : HomeRepository {
    // Serves AppDummyData as the temporary data source.
    // Can be seamlessly swapped with Retrofit API service calls later without touching UI or ViewModel.

    override fun getEarningsSummary(): Flow<Result<EarningsSummary>> = flow {
        emit(Result.success(AppDummyData.earningsSummary))
    }

    override fun getQuoteTabs(): Flow<Result<List<QuoteTabItem>>> = flow {
        emit(Result.success(AppDummyData.quoteVehicleTabs))
    }

    override fun getPromoBanners(): Flow<Result<List<PromoBanner>>> = flow {
        emit(Result.success(AppDummyData.promoBanners))
    }

    override fun getHomeMetrics(): Flow<Result<List<HomeMetric>>> = flow {
        emit(Result.success(AppDummyData.homeMetrics))
    }

    override fun getTopLeads(): Flow<Result<List<HomeLead>>> = flow {
        emit(Result.success(AppDummyData.topLeads))
    }

    override fun getRenewalLeads(): Flow<Result<List<HomeLead>>> = flow {
        emit(Result.success(AppDummyData.renewalLeads))
    }

    override fun getQuickLinks(): Flow<Result<List<QuickLink>>> = flow {
        emit(Result.success(AppDummyData.quickLinks))
    }

    override fun getTrustBullets(): Flow<Result<List<TrustBulletPoint>>> = flow {
        emit(Result.success(AppDummyData.trustBullets))
    }

    override fun getPartnerLogos(): Flow<Result<List<Int>>> = flow {
        emit(Result.success(AppDummyData.partnerLogos))
    }
}