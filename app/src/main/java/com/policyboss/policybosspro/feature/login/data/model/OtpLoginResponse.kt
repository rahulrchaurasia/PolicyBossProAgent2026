package com.policyboss.policybosspro.feature.login.data.model

import com.policyboss.policybosspro.core.serializer.LenientNullableStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
data class OtpUserMsg(
    @SerialName("Name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val name: String? = null,

    @SerialName("name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val nameLower: String? = null,

    @SerialName("Mobile_No")
    @Serializable(with = LenientNullableStringSerializer::class)
    val mobileNo: String? = null,

    @SerialName("mobile_no")
    @Serializable(with = LenientNullableStringSerializer::class)
    val mobileNoLower: String? = null,

    @SerialName("Ss_Id")
    val ssId: JsonElement? = null,

    @SerialName("ss_id")
    val ssIdLower: JsonElement? = null,

    @SerialName("OTP_Status")
    @Serializable(with = LenientNullableStringSerializer::class)
    val otpStatus: String? = null,

    @SerialName("otp_status")
    @Serializable(with = LenientNullableStringSerializer::class)
    val otpStatusLower: String? = null,

    @SerialName("Email_Id")
    @Serializable(with = LenientNullableStringSerializer::class)
    val emailId: String? = null,

    @SerialName("email_id")
    @Serializable(with = LenientNullableStringSerializer::class)
    val emailIdLower: String? = null
) {
    val effectiveName: String?
        get() = name ?: nameLower

    val effectiveMobileNo: String?
        get() = mobileNo ?: mobileNoLower

    val effectiveOtpStatus: String?
        get() = otpStatus ?: otpStatusLower

    val effectiveEmailId: String?
        get() = emailId ?: emailIdLower

    val effectiveSsIdElement: JsonElement?
        get() = ssId ?: ssIdLower

    /**
     * Safely extracts SS ID as a clean String.
     * Prevents literal "null", "NA", or "0" string artifacts.
     */
    val ssIdString: String?
        get() {
            val targetElement = effectiveSsIdElement ?: return null
            return when (targetElement) {
                is JsonNull -> null
                is JsonPrimitive -> {
                    val content = targetElement.content.trim().removeSurrounding("\"")
                    if (content.equals("null", ignoreCase = true) ||
                        content.equals("NA", ignoreCase = true) ||
                        content == "0" ||
                        content.isBlank()
                    ) {
                        null
                    } else {
                        content
                    }
                }
                else -> targetElement.toString().trim().removeSurrounding("\"")
                    .takeIf {
                        !it.equals("null", ignoreCase = true) &&
                        !it.equals("NA", ignoreCase = true) &&
                        it != "0" &&
                        it.isNotBlank()
                    }
            }
        }
}

@Serializable
data class OtpLoginResponse(
    @SerialName("Status")
    val status: String? = null,
    @SerialName("Msg")
    val msgElement: JsonElement? = null,
    @SerialName("status")
    val statusLower: String? = null,
    @SerialName("Message")
    val altMessage: String? = null,
    @SerialName("message")
    val lowerMessage: String? = null
) {
    companion object {
        val defaultJson = Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }
    }

    val effectiveStatus: String?
        get() = status ?: statusLower

    val userMsg: OtpUserMsg?
        get() = getUserMsg(defaultJson)

    fun getUserMsg(json: Json = defaultJson): OtpUserMsg? {
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
        get() {
            when (msgElement) {
                is JsonPrimitive -> {
                    val content = msgElement.content.trim().removeSurrounding("\"")
                    if (content.isNotBlank() &&
                        !content.equals("null", ignoreCase = true) &&
                        !content.equals("NA", ignoreCase = true) &&
                        !content.equals("FAIL", ignoreCase = true) &&
                        !content.equals("FAILURE", ignoreCase = true)
                    ) {
                        return content
                    }
                }
                is JsonObject -> {
                    val serverMsg = (msgElement["Msg"] as? JsonPrimitive)?.content
                        ?: (msgElement["msg"] as? JsonPrimitive)?.content
                        ?: (msgElement["Message"] as? JsonPrimitive)?.content
                        ?: (msgElement["message"] as? JsonPrimitive)?.content
                        ?: (msgElement["Error"] as? JsonPrimitive)?.content
                        ?: (msgElement["error"] as? JsonPrimitive)?.content
                    if (!serverMsg.isNullOrBlank()) {
                        val clean = serverMsg.trim().removeSurrounding("\"")
                        if (!clean.equals("FAIL", ignoreCase = true) && !clean.equals("FAILURE", ignoreCase = true)) {
                            return clean
                        }
                    }
                    val otpStatus = (msgElement["OTP_Status"] as? JsonPrimitive)?.content
                        ?: (msgElement["otp_status"] as? JsonPrimitive)?.content
                    if (otpStatus != null && !otpStatus.equals("SUCCESS", ignoreCase = true)) {
                        return "OTP Status: $otpStatus"
                    }
                }
                else -> {}
            }
            val alt = altMessage ?: lowerMessage
            if (!alt.isNullOrBlank()) {
                val clean = alt.trim().removeSurrounding("\"")
                if (!clean.equals("FAIL", ignoreCase = true) && !clean.equals("FAILURE", ignoreCase = true)) {
                    return clean
                }
            }
            return null
        }

    val isSuccessful: Boolean
        get() {
            if (!effectiveStatus.equals("SUCCESS", ignoreCase = true)) return false
            val user = getUserMsg() ?: return false
            val otpStat = user.effectiveOtpStatus ?: user.otpStatus
            return otpStat == null || otpStat.equals("SUCCESS", ignoreCase = true)
        }
}
