package com.policyboss.policybosspro.feature.carInsurance.data.model

import com.policyboss.policybosspro.core.serializer.LenientNullableStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response of `GET vehicles/beta_GetFuelVariant/{modelId}/{productId}/{makeName}`.
 *
 * Variant records carry many irregular keys (`IsActive` vs `Is_Active`, `undefined`,
 * dynamic `Insurer_*` / `Age_*` objects); only the fields below are read and the app-wide
 * Json config ignores the rest. Every scalar is lenient (numbers, strings, nulls).
 */
@Serializable
data class FuelVariantResponseDto(
    @SerialName("FuelList")
    val fuelList: List<@Serializable(with = LenientNullableStringSerializer::class) String?> = emptyList(),
    @SerialName("VariantList")
    val variantList: List<VariantItemDto> = emptyList()
)

@Serializable
data class VariantItemDto(
    @SerialName("Vehicle_ID")
    @Serializable(with = LenientNullableStringSerializer::class)
    val vehicleId: String? = null,
    @SerialName("Variant_Name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val variantName: String? = null,
    @SerialName("Model_ID")
    @Serializable(with = LenientNullableStringSerializer::class)
    val modelId: String? = null,
    @SerialName("Cubic_Capacity")
    @Serializable(with = LenientNullableStringSerializer::class)
    val cubicCapacity: String? = null,
    @SerialName("Fuel_ID")
    @Serializable(with = LenientNullableStringSerializer::class)
    val fuelId: String? = null,
    @SerialName("Seating_Capacity")
    @Serializable(with = LenientNullableStringSerializer::class)
    val seatingCapacity: String? = null,
    @SerialName("ExShoroomPrice")
    @Serializable(with = LenientNullableStringSerializer::class)
    val exShowroomPrice: String? = null,
    @SerialName("Make_Name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val makeName: String? = null,
    @SerialName("Model_Name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val modelName: String? = null,
    @SerialName("Fuel_Name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val fuelName: String? = null,
    @SerialName("Description")
    @Serializable(with = LenientNullableStringSerializer::class)
    val description: String? = null
)

/**
 * One record of `GET vehicles/get_preferred/make_model?ss_id=&product_id=1`
 * (the agent's preferred Make/Models, shown first in the sheet).
 */
@Serializable
data class PreferredMakeModelDto(
    @SerialName("Make_Name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val makeName: String? = null,
    @SerialName("Model_Name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val modelName: String? = null,
    @SerialName("Model_ID")
    @Serializable(with = LenientNullableStringSerializer::class)
    val modelId: String? = null,
    @SerialName("Count_Make_Model")
    @Serializable(with = LenientNullableStringSerializer::class)
    val countMakeModel: String? = null
)
