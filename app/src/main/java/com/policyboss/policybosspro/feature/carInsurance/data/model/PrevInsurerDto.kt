package com.policyboss.policybosspro.feature.carInsurance.data.model

import com.policyboss.policybosspro.core.serializer.LenientNullableStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One insurer from `GET insurers/getPrevInsurer/{productId}`.
 * The endpoint returns an object keyed by index ("0", "1", ...), not an array; the
 * repository reads the values in key order.
 */
@Serializable
data class PrevInsurerDto(
    @SerialName("insurer_id")
    @Serializable(with = LenientNullableStringSerializer::class)
    val insurerId: String? = null,
    @SerialName("insurer_name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val insurerName: String? = null
)
