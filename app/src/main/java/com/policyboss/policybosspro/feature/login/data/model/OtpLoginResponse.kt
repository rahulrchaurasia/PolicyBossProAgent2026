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
     * Strictly accepts only non-null primitive values and filters out
     * dirty literals ("NA", "N/A", "null", "undefined", "0", blank) as well as
     * non-primitive JSON structures (arrays/objects).
     */
    val ssIdString: String?
        get() {
            val element = effectiveSsIdElement ?: return null

            if (element !is JsonPrimitive || element is JsonNull) {
                return null
            }

            return element.content
                .trim()
                .removeSurrounding("\"")
                .trim()
                .takeIf { value ->
                    value.isNotBlank() &&
                        !value.equals("NA", ignoreCase = true) &&
                        !value.equals("N/A", ignoreCase = true) &&
                        !value.equals("null", ignoreCase = true) &&
                        !value.equals("undefined", ignoreCase = true) &&
                        value != "0"
                }
        }
}

private fun JsonElement?.cleanPrimitiveOrNull(): String? {
    if (this !is JsonPrimitive || this is JsonNull) return null
    return content
        .trim()
        .removeSurrounding("\"")
        .trim()
        .takeIf { value ->
            value.isNotBlank() &&
                !value.equals("NA", ignoreCase = true) &&
                !value.equals("N/A", ignoreCase = true) &&
                !value.equals("null", ignoreCase = true) &&
                !value.equals("undefined", ignoreCase = true)
        }
}

@Serializable
data class OtpLoginResponse(
    @SerialName("Status")
    @Serializable(with = LenientNullableStringSerializer::class)
    val status: String? = null,
    @SerialName("Msg")
    val msgElement: JsonElement? = null,
    @SerialName("status")
    @Serializable(with = LenientNullableStringSerializer::class)
    val statusLower: String? = null,
    @SerialName("Message")
    @Serializable(with = LenientNullableStringSerializer::class)
    val altMessage: String? = null,
    @SerialName("message")
    @Serializable(with = LenientNullableStringSerializer::class)
    val lowerMessage: String? = null,
    @SerialName("msg")
    val msgLowerElement: JsonElement? = null
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

    val effectiveMsgElement: JsonElement?
        get() = msgElement?.takeUnless { it is JsonNull } ?: msgLowerElement?.takeUnless { it is JsonNull }

    val userMsg: OtpUserMsg?
        get() = getUserMsg(defaultJson)

    fun getUserMsg(json: Json = defaultJson): OtpUserMsg? {
        val targetMsg = effectiveMsgElement ?: return null
        return try {
            if (targetMsg is JsonObject) {
                json.decodeFromJsonElement<OtpUserMsg>(targetMsg)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    val errorMessage: String?
        get() {
            when (val targetMsg = effectiveMsgElement) {
                is JsonPrimitive -> {
                    val content = targetMsg.cleanPrimitiveOrNull()
                    if (content != null &&
                        !content.equals("FAIL", ignoreCase = true) &&
                        !content.equals("FAILURE", ignoreCase = true)
                    ) {
                        return content
                    }
                }
                is JsonObject -> {
                    val serverMsg = targetMsg["Msg"].cleanPrimitiveOrNull()
                        ?: targetMsg["msg"].cleanPrimitiveOrNull()
                        ?: targetMsg["Message"].cleanPrimitiveOrNull()
                        ?: targetMsg["message"].cleanPrimitiveOrNull()
                        ?: targetMsg["Error"].cleanPrimitiveOrNull()
                        ?: targetMsg["error"].cleanPrimitiveOrNull()
                    if (serverMsg != null &&
                        !serverMsg.equals("FAIL", ignoreCase = true) &&
                        !serverMsg.equals("FAILURE", ignoreCase = true)
                    ) {
                        return serverMsg
                    }
                    val otpStatus = targetMsg["OTP_Status"].cleanPrimitiveOrNull()
                        ?: targetMsg["otp_status"].cleanPrimitiveOrNull()
                    if (otpStatus != null && !otpStatus.equals("SUCCESS", ignoreCase = true)) {
                        return "OTP Status: $otpStatus"
                    }
                }
                else -> {}
            }
            val alt = (altMessage ?: lowerMessage)
                ?.trim()
                ?.removeSurrounding("\"")
                ?.trim()
                ?.takeIf { value ->
                    value.isNotBlank() &&
                        !value.equals("NA", ignoreCase = true) &&
                        !value.equals("N/A", ignoreCase = true) &&
                        !value.equals("null", ignoreCase = true) &&
                        !value.equals("undefined", ignoreCase = true)
                }
            if (alt != null &&
                !alt.equals("FAIL", ignoreCase = true) &&
                !alt.equals("FAILURE", ignoreCase = true)
            ) {
                return alt
            }
            return null
        }

    val isSuccessful: Boolean
        get() {
            if (!effectiveStatus.equals("SUCCESS", ignoreCase = true)) {
                return false
            }

            val user = getUserMsg() ?: return false
            val otpStatus = user.effectiveOtpStatus

            return otpStatus == null || otpStatus.equals("SUCCESS", ignoreCase = true)
        }
}
