package com.policyboss.policybosspro.feature.register.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    @SerialName("FirstName") val FirstName: String,
    @SerialName("LastName") val LastName: String,
    @SerialName("Mobile_1") val Mobile_1: String,
    @SerialName("EmailId") val EmailId: String,
    @SerialName("DOB") val DOB: String,
    @SerialName("PinCode") val PinCode: String,
    @SerialName("City") val City: String = "",
    @SerialName("Posp_PAN") val Posp_PAN: String,
    @SerialName("Gender") val Gender: String,
    @SerialName("Source") val Source: String = "All-In-One(₹999)",
    @SerialName("field_sales_uid") val field_sales_uid: String = ""
)

