package com.policyboss.policybosspro.feature.login.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DevicetokenResponse(
    @SerialName("Status")
    val status: String? = null
)

