package com.policyboss.policybosspro.feature.carInsurance.data.model

import com.policyboss.policybosspro.core.serializer.LenientNullableStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One record of `GET vehicles/model_list?product_id=1`.
 * Fields are nullable so a single malformed record can be skipped instead of failing the list.
 */
@Serializable
data class VehicleModelItemDto(
    @SerialName("Model_ID")
    val modelId: Int? = null,
    @SerialName("Make_Name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val makeName: String? = null,
    @SerialName("Model_Name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val modelName: String? = null,
    @SerialName("vehicle_name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val vehicleName: String? = null
)
