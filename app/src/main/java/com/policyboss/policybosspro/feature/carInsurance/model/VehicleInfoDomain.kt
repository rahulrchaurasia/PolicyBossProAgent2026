package com.policyboss.policybosspro.feature.carInsurance.model

import androidx.compose.runtime.Immutable

/**
 * Clean, UI-agnostic vehicle details resolved from the vahan lookup
 * (`quote/vehicle_info_user`). Dates are already in the form's `dd-MMM-yyyy` format and
 * every value is either meaningful or blank — never a raw API placeholder.
 */
@Immutable
data class VehicleInfoDomain(
    val registrationNumber: String,
    val vahanSummary: String,
    val ownerName: String,
    val makeName: String,
    val modelName: String,
    val variantName: String,
    val fuelType: String,
    val modelId: String,
    val variantId: String,
    val fuelId: String,
    val cubicCapacity: String,
    val rtoCode: String,
    val rtoName: String,
    val registrationDate: String,
    val manufacturingMonth: String,
    val manufacturingYear: String,
    val previousInsurerId: String,
    val previousInsurerName: String,
    val policyExpiryDate: String,
    val vehicleDetailId: String
) {
    val hasExistingPolicy: Boolean
        get() = previousInsurerName.isNotBlank()

    /** `MARUTI-WAGON R-VXI+-PETROL- (998 CC)` */
    val carDetailsSummary: String
        get() {
            val base = listOf(makeName, modelName, variantName, fuelType)
                .filter { it.isNotBlank() }
                .joinToString("-")
            return if (cubicCapacity.isNotBlank()) "$base- ($cubicCapacity CC)" else base
        }

    /**
     * True when every field that the WITH_CAR_NUMBER form locks (customer name, RTO,
     * registration date, manufacturing month/year) is available. When false the form must
     * stay editable so the user can complete the missing values.
     */
    val hasAllLockedFields: Boolean
        get() = ownerName.isNotBlank() &&
            (rtoCode.isNotBlank() || rtoName.isNotBlank()) &&
            registrationDate.isNotBlank() &&
            manufacturingMonth.isNotBlank() &&
            manufacturingYear.isNotBlank()
}

/**
 * One live Make/Model record from `vehicles/model_list`.
 */
@Immutable
data class VehicleModel(
    val modelId: String,
    val makeName: String,
    val modelName: String,
    val vehicleName: String
)
