package com.policyboss.policybosspro.feature.login.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OtpLoginRequest(
    @SerialName("login_id")
    @SerializedName("login_id")
    val loginId: String,

    @SerialName("device_id")
    @SerializedName("device_id")
    val deviceId: String? = null,

    @SerialName("ip_address")
    @SerializedName("ip_address")
    val ipAddress: String? = null
)

