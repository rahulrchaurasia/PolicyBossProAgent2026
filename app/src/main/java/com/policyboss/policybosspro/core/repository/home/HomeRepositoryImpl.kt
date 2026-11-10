package com.policyboss.policybosspro.core.repository.home

import android.util.Log
import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.home.model.home.EarningsSummary
import com.policyboss.policybosspro.feature.home.model.home.HomeLead
import com.policyboss.policybosspro.feature.home.model.home.HomeMetric
import com.policyboss.policybosspro.feature.home.model.home.PromoBanner
import com.policyboss.policybosspro.feature.home.model.home.QuickLink
import com.policyboss.policybosspro.feature.home.model.home.TrustBulletPoint
import com.policyboss.policybosspro.feature.home.model.menu.QuoteTabItem
import com.policyboss.policybosspro.feature.login.data.api.LoginApi
import com.policyboss.policybosspro.feature.login.data.model.UserConstantResponse
import com.policyboss.policybosspro.utils.AppVersionProvider
import com.policyboss.policybosspro.utils.DeviceIdProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val loginApi: LoginApi,
    private val appDataManager: AppDataManager,
    private val appVersionProvider: AppVersionProvider,
    private val deviceIdProvider: DeviceIdProvider
) : HomeRepository {

    companion object {
        private const val TAG = "HomeRepository"
    }

    override suspend fun fetchAndSaveUserConstants(): Result<UserConstantResponse?> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            val ssId = appDataManager.getSsId()
            val fbaId = appDataManager.getFbaId()
            val deviceId = deviceIdProvider.getDeviceId()
            val appVersion = appVersionProvider.getAppVersion()

            val requestBody = mapOf(
                "app_version" to appVersion,
                "device_code" to deviceId,
                "ssid" to ssId,
                "fbaid" to fbaId
            )

            val response = loginApi.getUserConstant(requestBody)
            if (response.isSuccessful) {
                val body = response.body()
                Log.d(TAG, "User constants fetched successfully: ${body?.message}")
                body
            } else {
                val errorMsg = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                    ?: "Failed to fetch user constants: HTTP ${response.code()} ${response.message()}"
                Log.w(TAG, "User constants fetch failed: $errorMsg")
                throw Exception(errorMsg)
            }
        }
    }

    override suspend fun fetchAndSaveUserConstants(body: Map<String, String>): Response<UserConstantResponse>? = withContext(Dispatchers.IO) {
        return@withContext try {
            loginApi.getUserConstant(body)
        } catch (e: Exception) {
            Log.w(TAG, "Error fetching user constants with custom body: ${e.message}")
            null
        }
    }

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