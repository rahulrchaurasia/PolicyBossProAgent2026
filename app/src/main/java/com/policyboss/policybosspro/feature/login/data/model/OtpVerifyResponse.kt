package com.policyboss.policybosspro.feature.login.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OtpVerifyResponse(
    @SerialName("Msg")
    val msg: String? = null
) {
    val isSuccess: Boolean
        get() = msg?.equals("SUCCESS", ignoreCase = true) == true
}

