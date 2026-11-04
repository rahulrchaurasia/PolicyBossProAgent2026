package com.policyboss.policybosspro.feature.syncContact.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SyncContactAgreementResponse(
    @SerialName("Message")
    val message: String? = null,
    @SerialName("Status")
    val status: String? = null,
    @SerialName("StatusNo")
    val statusNo: Int? = null,
    @SerialName("Msg")
    val msg: List<SyncContactAgreementItem>? = null
)

@Serializable
data class SyncContactAgreementItem(
    @SerialName("ss_id")
    val ssId: Int? = null,
    @SerialName("fba_id")
    val fbaId: Int? = null,
    @SerialName("is_sms")
    val isSms: String? = null,
    @SerialName("is_call")
    val isCall: String? = null
)

