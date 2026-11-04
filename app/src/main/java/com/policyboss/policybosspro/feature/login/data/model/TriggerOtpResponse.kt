package com.policyboss.policybosspro.feature.login.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TriggerOtpResponse(
    @SerialName("Msg")
    val msg: String? = null
) {
    val isSuccess: Boolean
        get() = msg.equals("Success", ignoreCase = true)
}

