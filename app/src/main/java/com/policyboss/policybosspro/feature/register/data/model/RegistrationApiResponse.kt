package com.policyboss.policybosspro.feature.register.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class RegistrationApiResponse(
    @SerialName("Status") val Status: String = "",
    @SerialName("Message") val Message: String? = null,
    @SerialName("Msg") val Msg: String? = null,
    @SerialName("MasterData") val MasterData: MasterData? = null
) {
    val isSuccess: Boolean
        get() = Status.equals("SUCCESS", ignoreCase = true)

    val displayMessage: String
        get() = Message?.takeIf { it.isNotBlank() }
            ?: Msg?.takeIf { it.isNotBlank() }
            ?: "Operation completed successfully."
}

@Serializable
data class MasterData(
    @SerialName("FBAID")
    val rawFbaId: JsonPrimitive? = null
) {
    val fbaId: String?
        get() = rawFbaId?.content?.trim()?.removeSurrounding("\"")
}

