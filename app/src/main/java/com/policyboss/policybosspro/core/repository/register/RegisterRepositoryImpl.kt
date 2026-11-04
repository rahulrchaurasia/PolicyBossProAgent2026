package com.policyboss.policybosspro.core.repository.register

import com.policyboss.policybosspro.feature.register.data.api.RegisterApi
import com.policyboss.policybosspro.feature.register.data.model.CheckPospDataRequest
import com.policyboss.policybosspro.feature.register.data.model.RegistrationApiResponse
import com.policyboss.policybosspro.feature.register.data.model.RegistrationRequest
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisterRepositoryImpl @Inject constructor(
    private val registerApi: RegisterApi
) : RegisterRepository {

    override suspend fun checkDataAvailability(
        request: CheckPospDataRequest
    ): Result<RegistrationApiResponse> {
        return try {
            val response = registerApi.checkDataAvailability(request)

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("Server error (${response.code()}): ${response.message().ifBlank { "Data availability check failed" }}")
                )
            }

            val body = response.body()
                ?: return Result.failure(Exception("Empty response received from availability check."))

            if (body.isSuccess) {
                Result.success(body)
            } else {
                val errorMessage = body.displayMessage.ifBlank {
                    "Entered Email, Mobile, or PAN is already registered. Please check and try again."
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error. Please check your internet connection."))
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "An unexpected error occurred during availability check."))
        }
    }

    override suspend fun submitRegistration(
        request: RegistrationRequest
    ): Result<RegistrationApiResponse> {
        return try {
            val response = registerApi.submitRegistration(request)

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("Server error (${response.code()}): ${response.message().ifBlank { "Registration submission failed" }}")
                )
            }

            val body = response.body()
                ?: return Result.failure(Exception("Empty response received from registration server."))

            if (body.isSuccess) {
                Result.success(body)
            } else {
                val errorMessage = body.displayMessage.ifBlank {
                    "Registration submission failed. Please verify your details."
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error. Please check your internet connection."))
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "An unexpected error occurred during registration."))
        }
    }
}

