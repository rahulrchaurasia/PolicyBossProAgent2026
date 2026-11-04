package com.policyboss.policybosspro.feature.login.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
data class OtpUserMsg(
    @SerialName("Name")
    val name: String? = null,
    @SerialName("Mobile_No")
    val mobileNo: String? = null,
    @SerialName("Ss_Id")
    val ssId: JsonElement? = null,
    @SerialName("OTP_Status")
    val otpStatus: String? = null,
    @SerialName("Email_Id")
    val emailId: String? = null
) {
    val ssIdString: String?
        get() = when (ssId) {
            is JsonPrimitive -> ssId.content
            else -> ssId?.toString()
        }
}

@Serializable
data class OtpLoginResponse(
    @SerialName("Status")
    val status: String? = null,
    @SerialName("Msg")
    val msgElement: JsonElement? = null
) {
    fun getUserMsg(json: Json = Json { ignoreUnknownKeys = true; isLenient = true }): OtpUserMsg? {
        return try {
            if (msgElement is JsonObject) {
                json.decodeFromJsonElement<OtpUserMsg>(msgElement)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    val errorMessage: String?
        get() = when (msgElement) {
            is JsonPrimitive -> msgElement.content
            is JsonObject -> {
                val otpStatus = (msgElement["OTP_Status"] as? JsonPrimitive)?.content
                if (otpStatus != null && !otpStatus.equals("SUCCESS", ignoreCase = true)) {
                    "OTP Status: $otpStatus"
                } else null
            }
            else -> null
        }

    val isSuccessful: Boolean
        get() {
            if (!status.equals("SUCCESS", ignoreCase = true)) return false
            val user = getUserMsg()
            return user?.otpStatus?.equals("SUCCESS", ignoreCase = true) == true
        }
}

