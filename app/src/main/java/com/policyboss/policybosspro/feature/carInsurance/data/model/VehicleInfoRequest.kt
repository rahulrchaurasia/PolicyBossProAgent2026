package com.policyboss.policybosspro.feature.carInsurance.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request body for `POST quote/vehicle_info_user` (registration number → vahan lookup).
 *
 * secret_key, client_key and session_id are fixed constants sent as-is in the body.
 */
@Serializable
data class VehicleInfoRequest(
    @SerialName("secret_key")
    val secretKey: String = SECRET_KEY,
    @SerialName("client_key")
    val clientKey: String = CLIENT_KEY,
    @SerialName("RegistrationNumber")
    val registrationNumber: String,
    @SerialName("product_id")
    val productId: Int = CAR_PRODUCT_ID,
    @SerialName("ss_id")
    val ssId: Int,
    @SerialName("source")
    val source: String = SOURCE,
    @SerialName("session_id")
    val sessionId: String = SESSION_ID,
    @SerialName("g-recaptcha-response")
    val recaptchaResponse: String = ""
) {
    companion object {
        const val CAR_PRODUCT_ID = 1
        const val SOURCE = "PB-BETA"
        const val SECRET_KEY = "SECRET-HZ07QRWY-JIBT-XRMQ-ZP95-J0RWP3DYRACW"
        const val CLIENT_KEY = "CLIENT-CNTP6NYE-CU9N-DUZW-CSPI-SH1IS4DOVHB9"
        const val SESSION_ID = "5UsBoitooSMhp9Hw1YoPolWd5yj3jFMt"
    }
}
