package com.policyboss.policybosspro.feature.login.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class UserConstantResponse(
    @SerialName("Status")
    val status: String? = null,
    @SerialName("StatusNo")
    val statusNo: Int? = null,
    @SerialName("Message")
    val message: String? = null,
    @SerialName("MasterData")
    val masterData: JsonElement? = null
)

