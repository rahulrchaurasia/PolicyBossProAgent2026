package com.policyboss.policybosspro.core.repository.login

import android.os.Build
import android.util.Log
import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.feature.login.data.api.LoginApi
import com.policyboss.policybosspro.feature.login.data.model.OtpLoginRequest
import com.policyboss.policybosspro.feature.login.data.model.OtpUserMsg
import com.policyboss.policybosspro.feature.login.data.model.OtpVerifyResponse
import com.policyboss.policybosspro.utils.AppVersionProvider
import com.policyboss.policybosspro.utils.DeviceIdProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepositoryImpl @Inject constructor(
    private val loginApi: LoginApi,
    private val appDataManager: AppDataManager,
    private val appVersionProvider: AppVersionProvider,
    private val deviceIdProvider: DeviceIdProvider
) : LoginRepository {

    override suspend fun initiateLogin(loginId: String): Result<OtpUserMsg> {
        return try {
            val response = loginApi.initiateLogin(
                request = OtpLoginRequest(loginId = loginId.trim())
            )

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("Server error (${response.code()}): ${response.message().ifBlank { "Failed to initiate login" }}")
                )
            }

            val body = response.body()
                ?: return Result.failure(Exception("Empty response received from server."))

            if (body.isSuccessful) {
                val userMsg = body.getUserMsg()
                if (userMsg != null) {
                    Result.success(userMsg)
                } else {
                    Result.failure(Exception("Unable to read user details from response."))
                }
            } else {
                val errorMsg = body.errorMessage
                    ?: "Login initiation failed. Please check your credentials."
                Result.failure(Exception(errorMsg))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error. Please check your internet connection."))
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "An unexpected error occurred during login."))
        }
    }

    override suspend fun otpVerifyHorizon(
        otp: String,
        loginInput: String
    ): Flow<Response<OtpVerifyResponse>> = flow {
        val response = loginApi.otpVerifyHorizon(
            otp = otp.trim(),
            loginInput = loginInput.trim()
        )
        emit(response)
    }.flowOn(Dispatchers.IO)

    override suspend fun getLoginDetailHorizon(ssId: String): Result<Boolean> {
        val cleanSsId = ssId.trim()
        if (cleanSsId.isBlank()) {
            return Result.failure(Exception("SSID not generated. Please contact admin"))
        }

        return withContext(Dispatchers.IO) {
            try {
                // 1. Fetch primary Horizon login details (GET posps/dsas/view/{userId})
                val response = loginApi.getLoginDsasHorizonDetails(cleanSsId)
                if (!response.isSuccessful) {
                    return@withContext Result.failure(
                        Exception("Server error (${response.code()}): ${response.message().ifBlank { "Failed to retrieve user details" }}")
                    )
                }

                val body = response.body()
                    ?: return@withContext Result.failure(Exception("Empty response from Horizon server."))

                if (!body.isSuccessful) {
                    return@withContext Result.failure(Exception("Failed to verify Horizon user profile."))
                }

                // 2. Extract resolved user details with multi-level fallbacks
                val resolvedSsId = body.extractedSsId.ifBlank { cleanSsId }
                val resolvedFbaId = body.extractedFbaId
                val resolvedName = body.extractedName
                val resolvedMobile = body.extractedMobile
                val resolvedEmail = body.extractedEmail
                val resolvedUserType = body.userType.orEmpty()

                // 3. Atomically persist full user identity & session state into AppDataManager
                appDataManager.saveUserSession(
                    name = resolvedName.takeIf { it.isNotBlank() },
                    mobile = resolvedMobile.takeIf { it.isNotBlank() },
                    email = resolvedEmail.takeIf { it.isNotBlank() },
                    ssId = resolvedSsId,
                    fbaId = resolvedFbaId,
                    userType = resolvedUserType.takeIf { it.isNotBlank() },
                    isLogin = true
                )

                // 4. Concurrently run parallel non-blocking syncs in supervisorScope
                supervisorScope {
                    val userConstantDeferred = async {
                        runCatching {
                            val requestBody = mapOf(
                                "app_version" to appVersionProvider.getAppVersion(),
                                "device_code" to deviceIdProvider.getDeviceId(),
                                "ssid" to resolvedSsId,
                                "fbaid" to resolvedFbaId
                            )
                            loginApi.getUserConstant(requestBody)
                        }
                    }

                    val insertTokenDeferred = async {
                        runCatching {
                            val requestBody = mapOf(
                                "Ss_Id" to resolvedSsId,
                                "Device_Id" to deviceIdProvider.getDeviceId(),
                                "Device_Name" to Build.MODEL,
                                "Token" to appDataManager.getAuthToken().ifBlank { "1234567890" }
                            )
                            loginApi.insertNotificationToken(requestBody)
                        }
                    }

                    // Await both safely; non-critical failures won't break the user's login session
                    userConstantDeferred.await().onSuccess { ucResp ->
                        if (ucResp.isSuccessful) {
                            Log.d("LoginRepo", "UserConstant sync successful")
                        }
                    }.onFailure { e ->
                        Log.w("LoginRepo", "UserConstant sync failed: ${e.message}")
                    }

                    insertTokenDeferred.await().onSuccess { tokenResp ->
                        if (tokenResp.isSuccessful) {
                            Log.d("LoginRepo", "Notification token registration successful")
                        }
                    }.onFailure { e ->
                        Log.w("LoginRepo", "Notification token registration failed: ${e.message}")
                    }
                }

                Result.success(true)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            appDataManager.clearUserSession()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
