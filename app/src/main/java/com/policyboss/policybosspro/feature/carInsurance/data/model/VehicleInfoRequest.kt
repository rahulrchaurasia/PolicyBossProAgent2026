package com.policyboss.policybosspro.feature.carInsurance.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request body for `POST quote/vehicle_info_user` (registration number → vahan lookup).
 *
 * Credentials are supplied by the repository from BuildConfig (local.properties);
 * never hardcode them here.
 */
@Serializable
data class VehicleInfoRequest(
    @SerialName("secret_key")
    val secretKey: String,
    @SerialName("client_key")
    val clientKey: String,
    @SerialName("RegistrationNumber")
    val registrationNumber: String,
    @SerialName("product_id")
    val productId: Int = CAR_PRODUCT_ID,
    @SerialName("ss_id")
    val ssId: Int,
    @SerialName("source")
    val source: String = SOURCE,
    @SerialName("session_id")
    val sessionId: String = "",
    @SerialName("g-recaptcha-response")
    val recaptchaResponse: String = ""
) {
    companion object {
        const val CAR_PRODUCT_ID = 1
        const val SOURCE = "PB-BETA"
    }
}
