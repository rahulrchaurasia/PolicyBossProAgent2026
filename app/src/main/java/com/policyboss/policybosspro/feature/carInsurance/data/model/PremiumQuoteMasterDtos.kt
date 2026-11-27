package com.policyboss.policybosspro.feature.carInsurance.data.model

import com.policyboss.policybosspro.core.serializer.LenientNullableBooleanSerializer
import com.policyboss.policybosspro.core.serializer.LenientNullableDoubleSerializer
import com.policyboss.policybosspro.core.serializer.LenientNullableIntSerializer
import com.policyboss.policybosspro.core.serializer.LenientNullableLongSerializer
import com.policyboss.policybosspro.core.serializer.LenientNullableStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer

/*
 * `Master` block of premium_initiate / premium_summary.
 *
 * Master.Vehicle and Master.Rto mix fixed fields with one `Insurer_<id>` entry per
 * mapped insurer. The transforming serializers below move those entries into
 * [PremiumVehicleMasterDto.insurers] / [PremiumRtoMasterDto.insurers], keyed by insurer id,
 * so both halves are typed. Insurer masters send the same field as a number, a string,
 * "" or null, so every scalar uses a lenient serializer.
 */

typealias LenientString = @Serializable(with = LenientNullableStringSerializer::class) String?
typealias LenientInt = @Serializable(with = LenientNullableIntSerializer::class) Int?
typealias LenientLong = @Serializable(with = LenientNullableLongSerializer::class) Long?
typealias LenientDouble = @Serializable(with = LenientNullableDoubleSerializer::class) Double?
typealias LenientBoolean = @Serializable(with = LenientNullableBooleanSerializer::class) Boolean?

@Serializable
data class PremiumMasterDto(
    @SerialName("Vehicle")
    @Serializable(with = PremiumVehicleMasterSerializer::class)
    val vehicle: PremiumVehicleMasterDto? = null,
    @SerialName("Rto")
    @Serializable(with = PremiumRtoMasterSerializer::class)
    val rto: PremiumRtoMasterDto? = null
)

// ---------------------------------------------------------------------
// Master.Vehicle
// ---------------------------------------------------------------------

@Serializable
data class PremiumVehicleMasterDto(
    @SerialName("Vehicle_ID") val vehicleId: LenientLong = null,
    @SerialName("Base_Vehicle_ID") val baseVehicleId: LenientLong = null,
    @SerialName("Is_Base") val isBase: LenientString = null,
    @SerialName("Make_ID") val makeId: LenientInt = null,
    @SerialName("Make_Name") val makeName: LenientString = null,
    @SerialName("Model_ID") val modelId: LenientInt = null,
    @SerialName("Model_Name") val modelName: LenientString = null,
    @SerialName("Variant_Name") val variantName: LenientString = null,
    @SerialName("Description") val description: LenientString = null,
    @SerialName("Fuel_ID") val fuelId: LenientInt = null,
    @SerialName("Fuel_Name") val fuelName: LenientString = null,
    @SerialName("Cubic_Capacity") val cubicCapacity: LenientInt = null,
    @SerialName("Seating_Capacity") val seatingCapacity: LenientInt = null,
    /** Spelled `ExShoroomPrice` by the backend. */
    @SerialName("ExShoroomPrice") val exShowroomPrice: LenientDouble = null,
    @SerialName("Vehicle_Image") val vehicleImage: LenientString = null,
    @SerialName("IsActive") val isActive: LenientInt = null,
    @SerialName("ModifyOn") val modifyOn: LenientString = null,
    @SerialName("IndirectVariant_Id") val indirectVariantId: LenientString = null,
    @SerialName("InHouseSS_Variant_Id") val inHouseSsVariantId: LenientString = null,
    @SerialName("Product_Id_New") val productIdNew: LenientInt = null,
    @SerialName("Product_Sub_Category_Code") val productSubCategoryCode: LenientInt = null,
    @SerialName("Capacity_Type") val capacityType: LenientString = null,
    @SerialName("Gross_Vehicle_Weight") val grossVehicleWeight: LenientDouble = null,
    @SerialName("Product_Type") val productType: LenientString = null,
    @SerialName("Product_Sub_Category_Class_CODE") val productSubCategoryClassCode: LenientInt = null,
    @SerialName("Product_Sub_Category_Class_Name") val productSubCategoryClassName: LenientString = null,
    @SerialName("Number_of_wheels") val numberOfWheels: LenientInt = null,
    @SerialName("Insurer_Vehicle_Insurer_Segmant") val insurerSegment: LenientString = null,
    @SerialName("Mapped_Insurer_Count") val mappedInsurerCount: LenientInt = null,
    @SerialName("Premium_Attempt_On") val premiumAttemptOn: LenientString = null,
    /** `Insurer_<id>` entries, keyed by insurer id. */
    @SerialName(INSURERS_KEY) val insurers: Map<Int, InsurerVehicleMappingDto> = emptyMap()
)

/** One `Master.Vehicle.Insurer_<id>` entry: how our vehicle maps to that insurer's master. */
@Serializable
data class InsurerVehicleMappingDto(
    @SerialName("Insurer_ID") val insurerId: LenientInt = null,
    @SerialName("Vehicle_ID") val vehicleId: LenientLong = null,
    @SerialName("Insurer_Vehicle_ID") val insurerVehicleId: LenientLong = null,
    /** Absent for some insurers (e.g. 100, 101). */
    @SerialName("VI_Master") val viMaster: InsurerVehicleMasterDto? = null,
    @SerialName("Premium_Status") val premiumStatus: LenientInt = null,
    @SerialName("Premium_Status_On") val premiumStatusOn: LenientString = null,
    @SerialName("Premium_Status_CRN") val premiumStatusCrn: LenientLong = null,
    @SerialName("Status_Id") val statusId: LenientInt = null,
    @SerialName("Is_Active") val isActive: LenientInt = null,
    @SerialName("Created_On") val createdOn: LenientString = null,
    @SerialName("Mapped_By") val mappedBy: LenientString = null
)

/** `VI_Master`: the insurer's own record for the vehicle. */
@Serializable
data class InsurerVehicleMasterDto(
    @SerialName("Insurer_Vehicle_ID") val insurerVehicleId: LenientLong = null,
    @SerialName("Insurer_ID") val insurerId: LenientInt = null,
    @SerialName("Insurer_Vehicle_Code") val code: LenientString = null,
    @SerialName("Insurer_Vehicle_Make_Name") val makeName: LenientString = null,
    @SerialName("Insurer_Vehicle_Make_Code") val makeCode: LenientString = null,
    @SerialName("Insurer_Vehicle_Model_Name") val modelName: LenientString = null,
    @SerialName("Insurer_Vehicle_Model_Code") val modelCode: LenientString = null,
    @SerialName("Insurer_Vehicle_Variant_Name") val variantName: LenientString = null,
    @SerialName("Insurer_Vehicle_Variant_Code") val variantCode: LenientString = null,
    @SerialName("Insurer_Vehicle_Insurer_BodyType") val bodyType: LenientString = null,
    @SerialName("Insurer_Vehicle_Insurer_BodyTypeCode") val bodyTypeCode: LenientString = null,
    @SerialName("Insurer_Vehicle_FuelType") val fuelType: LenientString = null,
    @SerialName("Insurer_Vehicle_FuelType_Code") val fuelTypeCode: LenientString = null,
    @SerialName("Insurer_Vehicle_SeatingCapacity") val seatingCapacity: LenientInt = null,
    @SerialName("Insurer_Vehicle_CubicCapacity") val cubicCapacity: LenientInt = null,
    @SerialName("Insurer_Vehicle_ExShowRoom") val exShowroom: LenientDouble = null,
    @SerialName("Insurer_Vehicle_Insurer_Segmant") val segment: LenientString = null,
    @SerialName("Insurer_Vehicle_Manufacture_Year") val manufactureYear: LenientInt = null,
    @SerialName("Insurer_Vehicle_Manufacture_Year_Start") val manufactureYearStart: LenientInt = null,
    @SerialName("Insurer_Vehicle_Manufacture_Year_End") val manufactureYearEnd: LenientInt = null,
    @SerialName("Is_Active") val isActive: LenientInt = null,
    @SerialName("Created_On") val createdOn: LenientString = null,
    @SerialName("Product_Id_New") val productIdNew: LenientInt = null,
    @SerialName("PB_Make_Name") val pbMakeName: LenientString = null,
    @SerialName("PB_Model_Name") val pbModelName: LenientString = null,
    @SerialName("PBboss_Make_Name") val pbBossMakeName: LenientString = null,
    @SerialName("PBboss_Model_Name") val pbBossModelName: LenientString = null,
    @SerialName("Vehicles_Insurers_Combine") val vehiclesInsurersCombine: LenientString = null,
    @SerialName("Product_Sub_Category") val productSubCategory: LenientInt = null,
    @SerialName("Product_Sub_Category_Code") val productSubCategoryCode: LenientInt = null,
    @SerialName("Product_Sub_Category_Class") val productSubCategoryClass: LenientInt = null,
    @SerialName("Product_Sub_Category_Class_Name") val productSubCategoryClassName: LenientString = null,
    @SerialName("CAPACITY_TYPE") val capacityType: LenientString = null,
    @SerialName("MASTER CODE (IDV CODE)") val idvMasterCode: LenientString = null,
    @SerialName("Gross_Vehicle_Weight") val grossVehicleWeight: LenientDouble = null,
    @SerialName("Number_of_wheels") val numberOfWheels: LenientInt = null
)

// ---------------------------------------------------------------------
// Master.Rto
// ---------------------------------------------------------------------

@Serializable
data class PremiumRtoMasterDto(
    @SerialName("VehicleCity_Id") val vehicleCityId: LenientInt = null,
    @SerialName("VehicleCity_RTOCode") val rtoCode: LenientString = null,
    @SerialName("RTO_City") val rtoCity: LenientString = null,
    @SerialName("State_Id") val stateId: LenientInt = null,
    @SerialName("State_Name") val stateName: LenientString = null,
    @SerialName("VehicleTariff_Zone") val tariffZone: LenientString = null,
    @SerialName("VehicleTariff_CV_Zone") val tariffCvZone: LenientString = null,
    @SerialName("VehicleTariff_NONCV_Zone") val tariffNonCvZone: LenientString = null,
    @SerialName("ERP_RegionName") val erpRegionName: LenientString = null,
    @SerialName("ERP_RegionID") val erpRegionId: LenientInt = null,
    @SerialName("IsActive") val isActive: LenientBoolean = null,
    @SerialName("CreatedOn") val createdOn: LenientString = null,
    /** `Insurer_<id>` entries, keyed by insurer id. */
    @SerialName(INSURERS_KEY) val insurers: Map<Int, InsurerRtoMappingDto> = emptyMap()
)

/** One `Master.Rto.Insurer_<id>` entry: how our RTO maps to that insurer's master. */
@Serializable
data class InsurerRtoMappingDto(
    @SerialName("Insurer_ID") val insurerId: LenientInt = null,
    @SerialName("Rto_ID") val rtoId: LenientInt = null,
    @SerialName("Insurer_Rto_ID") val insurerRtoId: LenientLong = null,
    @SerialName("Insurer_Rto_Mapping_ID") val insurerRtoMappingId: LenientLong = null,
    @SerialName("RI_Master") val riMaster: InsurerRtoMasterDto? = null,
    @SerialName("Premium_Status") val premiumStatus: LenientInt = null,
    @SerialName("Status_Id") val statusId: LenientInt = null,
    @SerialName("Is_Active") val isActive: LenientInt = null,
    @SerialName("Created_On") val createdOn: LenientString = null
)

/**
 * `RI_Master`: the insurer's own record for the RTO. Codes are numbers for some
 * insurers and strings for others, so they are all strings here.
 */
@Serializable
data class InsurerRtoMasterDto(
    @SerialName("Insurer_Rto_ID") val insurerRtoId: LenientLong = null,
    @SerialName("Insurer_ID") val insurerId: LenientInt = null,
    @SerialName("Insurer_Rto_Code") val rtoCode: LenientString = null,
    @SerialName("Insurer_Rto_City_Name") val cityName: LenientString = null,
    @SerialName("Insurer_Rto_City_Code") val cityCode: LenientString = null,
    @SerialName("Insurer_Rto_City_Car_Code") val cityCarCode: LenientString = null,
    @SerialName("Insurer_Rto_City_Code_Car") val cityCodeCar: LenientString = null,
    @SerialName("Insurer_Rto_City_Code_CV") val cityCodeCv: LenientString = null,
    @SerialName("Insurer_Rto_District_Name") val districtName: LenientString = null,
    @SerialName("Insurer_Rto_District_Code") val districtCode: LenientString = null,
    @SerialName("Insurer_Rto_District_CV_Code") val districtCvCode: LenientString = null,
    @SerialName("Insurer_Rto_District_TW") val districtTw: LenientString = null,
    @SerialName("Insurer_Rto_State_Name") val stateName: LenientString = null,
    @SerialName("Insurer_Rto_State_Code") val stateCode: LenientString = null,
    @SerialName("Insurer_Rto_Zone_Name") val zoneName: LenientString = null,
    @SerialName("Insurer_Rto_Zone_Code") val zoneCode: LenientString = null,
    @SerialName("Insurer_Rto_Zone_CV_Name") val zoneCvName: LenientString = null,
    @SerialName("Insurer_Rto_Location_Code") val locationCode: LenientString = null,
    @SerialName("Insurer_Rto_Region") val region: LenientString = null,
    @SerialName("Insurer_Rto_Pin_Code") val pinCode: LenientString = null,
    @SerialName("NUM_PINCODE") val numPincode: LenientString = null,
    @SerialName("IsActive") val isActive: LenientInt = null,
    @SerialName("Created_On") val createdOn: LenientString = null
)

// ---------------------------------------------------------------------
// Insurer_<id> grouping
// ---------------------------------------------------------------------

/** Synthetic key the `Insurer_<id>` entries are grouped under; never sent by the backend. */
private const val INSURERS_KEY = "insurers"
private const val INSURER_PREFIX = "Insurer_"

/** `Insurer_101` -> 101; null for fixed fields such as `Insurer_Vehicle_Insurer_Segmant`. */
private fun insurerIdOf(key: String): String? =
    key.takeIf { it.startsWith(INSURER_PREFIX) }?.removePrefix(INSURER_PREFIX)?.takeIf { it.toIntOrNull() != null }

private fun JsonElement.groupInsurerEntries(): JsonElement {
    if (this !is JsonObject) return this
    val insurers = mutableMapOf<String, JsonElement>()
    val fixed = mutableMapOf<String, JsonElement>()
    for ((key, value) in this) {
        val id = insurerIdOf(key)
        if (id != null) insurers[id] = value else fixed[key] = value
    }
    fixed[INSURERS_KEY] = JsonObject(insurers)
    return JsonObject(fixed)
}

private fun JsonElement.flattenInsurerEntries(): JsonElement {
    if (this !is JsonObject) return this
    val insurers = this[INSURERS_KEY] as? JsonObject ?: return this
    return JsonObject(
        filterKeys { it != INSURERS_KEY } + insurers.mapKeys { (id, _) -> INSURER_PREFIX + id }
    )
}

object PremiumVehicleMasterSerializer :
    JsonTransformingSerializer<PremiumVehicleMasterDto>(PremiumVehicleMasterDto.serializer()) {
    override fun transformDeserialize(element: JsonElement) = element.groupInsurerEntries()
    override fun transformSerialize(element: JsonElement) = element.flattenInsurerEntries()
}

object PremiumRtoMasterSerializer :
    JsonTransformingSerializer<PremiumRtoMasterDto>(PremiumRtoMasterDto.serializer()) {
    override fun transformDeserialize(element: JsonElement) = element.groupInsurerEntries()
    override fun transformSerialize(element: JsonElement) = element.flattenInsurerEntries()
}
