package com.policyboss.policybosspro.feature.carInsurance.data.model

import com.policyboss.policybosspro.core.serializer.LenientNullableStringSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject

/**
 * Response of `POST quote/vehicle_info_user`.
 *
 * Every scalar is decoded with [LenientNullableStringSerializer] so numbers, strings, blanks,
 * "NA" and nested objects in unexpected places all parse safely (non-scalars become null).
 * The large insurer-specific `Vehicle_Matches` array is kept as a raw [JsonElement] so its
 * shape can never break parsing. Unknown keys are ignored by the app-wide Json config.
 */
@Serializable
data class VehicleInfoResponseDto(
    @SerialName("Result")
    @Serializable(with = LenientNullableStringSerializer::class)
    val result: String? = null,
    @SerialName("status")
    @Serializable(with = LenientNullableStringSerializer::class)
    val status: String? = null,
    @SerialName("Error_Message")
    @Serializable(with = LenientNullableStringSerializer::class)
    val errorMessage: String? = null,
    @SerialName("Registration_Number")
    @Serializable(with = LenientNullableStringSerializer::class)
    val registrationNumber: String? = null,
    @SerialName("Make_Name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val makeName: String? = null,
    @SerialName("Model_Name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val modelName: String? = null,
    @SerialName("Fuel_Type")
    @Serializable(with = LenientNullableStringSerializer::class)
    val fuelType: String? = null,
    @SerialName("Variant_Name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val variantName: String? = null,
    @SerialName("Model_ID")
    @Serializable(with = LenientNullableStringSerializer::class)
    val modelId: String? = null,
    @SerialName("Variant_Id")
    @Serializable(with = LenientNullableStringSerializer::class)
    val variantId: String? = null,
    @SerialName("Fuel_ID")
    @Serializable(with = LenientNullableStringSerializer::class)
    val fuelId: String? = null,
    @SerialName("Cubic_Capacity")
    @Serializable(with = LenientNullableStringSerializer::class)
    val cubicCapacity: String? = null,
    @SerialName("Seating_Capacity")
    @Serializable(with = LenientNullableStringSerializer::class)
    val seatingCapacity: String? = null,
    @SerialName("Manufacture_Year")
    @Serializable(with = LenientNullableStringSerializer::class)
    val manufactureYear: String? = null,
    @SerialName("Manufacture_Month")
    @Serializable(with = LenientNullableStringSerializer::class)
    val manufactureMonth: String? = null,
    @SerialName("Registration_Date")
    @Serializable(with = LenientNullableStringSerializer::class)
    val registrationDate: String? = null,
    @SerialName("RTO_Code")
    @Serializable(with = LenientNullableStringSerializer::class)
    val rtoCode: String? = null,
    @SerialName("RTO_Name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val rtoName: String? = null,
    @SerialName("Owner_Name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val ownerName: String? = null,
    @SerialName("Pyp_Policy_Expiry_Date")
    @Serializable(with = LenientNullableStringSerializer::class)
    val pypPolicyExpiryDate: String? = null,
    @SerialName("Pyp_Insurer_Name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val pypInsurerName: String? = null,
    @SerialName("Pyp_Insurer_Id")
    @Serializable(with = LenientNullableStringSerializer::class)
    val pypInsurerId: String? = null,
    @SerialName("Pyp_Policy_Number")
    @Serializable(with = LenientNullableStringSerializer::class)
    val pypPolicyNumber: String? = null,
    @SerialName("Vahan_Make")
    @Serializable(with = LenientNullableStringSerializer::class)
    val vahanMake: String? = null,
    @SerialName("Vahan_Model")
    @Serializable(with = LenientNullableStringSerializer::class)
    val vahanModel: String? = null,
    @SerialName("Vahan_Variant")
    @Serializable(with = LenientNullableStringSerializer::class)
    val vahanVariant: String? = null,
    @SerialName("Vahan_Fuel")
    @Serializable(with = LenientNullableStringSerializer::class)
    val vahanFuel: String? = null,
    @SerialName("Vahan_Cubic_Capacity")
    @Serializable(with = LenientNullableStringSerializer::class)
    val vahanCubicCapacity: String? = null,
    @SerialName("Vehicle_Detail_Id")
    @Serializable(with = LenientNullableStringSerializer::class)
    val vehicleDetailId: String? = null,
    @SerialName("Chassis_Number")
    @Serializable(with = LenientNullableStringSerializer::class)
    val chassisNumber: String? = null,
    @SerialName("Engin_Number")
    @Serializable(with = LenientNullableStringSerializer::class)
    val engineNumber: String? = null,
    @SerialName("Permanent_Pincode")
    @Serializable(with = LenientNullableStringSerializer::class)
    val permanentPincode: String? = null,
    @SerialName("Vahan_CC_Slab")
    @Serializable(with = LenientNullableStringSerializer::class)
    val vahanCcSlab: String? = null,
    @SerialName("Source")
    @Serializable(with = LenientNullableStringSerializer::class)
    val source: String? = null,
    @SerialName("FastlaneResponse_Obj")
    @Serializable(with = LenientFastlaneResponseObjSerializer::class)
    val fastlaneResponse: FastlaneResponseObjDto? = null,
    @SerialName("Vehicle_Matches")
    val vehicleMatches: JsonElement? = null
)

/**
 * Nested `FastlaneResponse_Obj` (raw RC data from the vahan provider).
 */
@Serializable
data class FastlaneResponseObjDto(
    @SerialName("maker_description")
    @Serializable(with = LenientNullableStringSerializer::class)
    val makerDescription: String? = null,
    @SerialName("maker_model")
    @Serializable(with = LenientNullableStringSerializer::class)
    val makerModel: String? = null,
    @SerialName("fuel_type")
    @Serializable(with = LenientNullableStringSerializer::class)
    val fuelType: String? = null,
    @SerialName("cubic_capacity")
    @Serializable(with = LenientNullableStringSerializer::class)
    val cubicCapacity: String? = null,
    @SerialName("owner_name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val ownerName: String? = null,
    @SerialName("registration_date")
    @Serializable(with = LenientNullableStringSerializer::class)
    val registrationDate: String? = null,
    @SerialName("manufacturing_date")
    @Serializable(with = LenientNullableStringSerializer::class)
    val manufacturingDate: String? = null,
    @SerialName("manufacturing_date_formatted")
    @Serializable(with = LenientNullableStringSerializer::class)
    val manufacturingDateFormatted: String? = null,
    @SerialName("insurance_company")
    @Serializable(with = LenientNullableStringSerializer::class)
    val insuranceCompany: String? = null,
    @SerialName("insurance_upto")
    @Serializable(with = LenientNullableStringSerializer::class)
    val insuranceUpto: String? = null,
    @SerialName("rc_number")
    @Serializable(with = LenientNullableStringSerializer::class)
    val rcNumber: String? = null
)

/**
 * Decodes `FastlaneResponse_Obj` only when it is a JSON object; a string, array, blank
 * or null value (seen when the provider has no RC data) becomes null instead of failing.
 */
object LenientFastlaneResponseObjSerializer : KSerializer<FastlaneResponseObjDto?> {

    override val descriptor: SerialDescriptor = FastlaneResponseObjDto.serializer().descriptor

    override fun deserialize(decoder: Decoder): FastlaneResponseObjDto? {
        val jsonDecoder = decoder as? JsonDecoder
            ?: return FastlaneResponseObjDto.serializer().deserialize(decoder)
        val element = jsonDecoder.decodeJsonElement()
        if (element !is JsonObject) return null
        return runCatching {
            jsonDecoder.json.decodeFromJsonElement(FastlaneResponseObjDto.serializer(), element)
        }.getOrNull()
    }

    override fun serialize(encoder: Encoder, value: FastlaneResponseObjDto?) {
        val jsonEncoder = encoder as? JsonEncoder
        when {
            value == null && jsonEncoder != null -> jsonEncoder.encodeJsonElement(JsonNull)
            value != null -> FastlaneResponseObjDto.serializer().serialize(encoder, value)
        }
    }
}
