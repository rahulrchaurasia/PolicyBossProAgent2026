package com.policyboss.policybosspro.feature.syncContact.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveCheckboxRequestEntity(
    @SerialName("fba_id")
    val fba_id: Int = 0,
    @SerialName("is_sms")
    val is_sms: String = "no",
    @SerialName("is_call")
    val is_call: String = "no",
    @SerialName("online_agreement")
    val online_agreement: String = "online_agreement",
    @SerialName("ss_id")
    val ss_id: Int = 0,
    @SerialName("app_version")
    val app_version: String = "",
    @SerialName("device_code")
    val device_code: String = ""
)

@Serializable
data class SaveCheckboxResponseEntity(
    @SerialName("Message")
    val message: String? = null,
    @SerialName("Status")
    val status: String? = null,
    @SerialName("StatusNo")
    val statusNo: Int? = null
)

