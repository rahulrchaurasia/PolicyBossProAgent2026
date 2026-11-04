package com.policyboss.policybosspro.feature.login.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OtpLoginRequest(
    @SerialName("login_id")
    val loginId: String
)

