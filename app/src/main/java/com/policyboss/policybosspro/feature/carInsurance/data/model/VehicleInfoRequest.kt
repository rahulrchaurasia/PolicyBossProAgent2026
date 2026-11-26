package com.policyboss.policybosspro.feature.carInsurance.data.model

import com.policyboss.policybosspro.core.network.NetworkConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request body for `POST quote/vehicle_info_user` (registration number → vahan lookup).
 *
 * Uses centralized security credentials defined in [NetworkConstants].
 */
@Serializable
data class VehicleInfoRequest(
    @SerialName("secret_key")
    val secretKey: String = NetworkConstants.SECRET_KEY,
    @SerialName("client_key")
    val clientKey: String = NetworkConstants.CLIENT_KEY,
    @SerialName("RegistrationNumber")
    val registrationNumber: String,
    @SerialName("product_id")
    val productId: Int = CAR_PRODUCT_ID,
    @SerialName("ss_id")
    val ssId: Int,
    @SerialName("source")
    val source: String = SOURCE,
    @SerialName("session_id")
    val sessionId: String = NetworkConstants.SESSION_ID,
    @SerialName("g-recaptcha-response")
    val recaptchaResponse: String = ""
) {
    companion object {
        const val CAR_PRODUCT_ID = 1
        const val SOURCE = "PB-BETA"

        // Centralized reference aliases from NetworkConstants for convenience and backward compatibility
        const val SECRET_KEY = NetworkConstants.SECRET_KEY
        const val CLIENT_KEY = NetworkConstants.CLIENT_KEY
        const val SESSION_ID = NetworkConstants.SESSION_ID
    }
}
