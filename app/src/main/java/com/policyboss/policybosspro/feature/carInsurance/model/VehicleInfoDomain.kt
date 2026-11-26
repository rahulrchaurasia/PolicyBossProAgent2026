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
    val vehicleDetailId: String,
    /** Raw values kept for `quote/premium_initiate`; API formats, blank when unknown. */
    val premiumData: VehiclePremiumData = VehiclePremiumData()
) {
    val hasExistingPolicy: Boolean
        get() = previousInsurerName.isNotBlank()

    /** `MARUTI-WAGON R-VXI+-PETROL- (998 CC)` */
    val carDetailsSummary: String
        get() = com.policyboss.policybosspro.feature.carInsurance.data.mapper.buildCarDetailsSummary(
            makeName, modelName, variantName, fuelType, cubicCapacity
        )

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
 * Vahan values needed only by `quote/premium_initiate`. Dates are `yyyy-MM-dd`
 * (manufacture date is `yyyy-MM-01`); every value is blank when the vahan lookup had none.
 */
@Immutable
data class VehiclePremiumData(
    val registrationDateApi: String = "",
    val manufactureDateApi: String = "",
    val policyExpiryDateApi: String = "",
    val previousPolicyNumber: String = "",
    val chassisNumber: String = "",
    val engineNumber: String = "",
    val permanentPincode: String = "",
    val vahanCcSlab: String = "",
    val vahanSource: String = "",
    val vahanCubicCapacity: String = "",
    /** `MARUTI SUZUKI INDIA LTD|MARUTI WAGON R VXI BSIV|PETROL|998CC` */
    val vahanFull: String = "",
    /** `MARUTI|WAGON R|VXI+|PETROL|998 CC` */
    val matchVehicleFull: String = "",
    /** vahan Result/status was Success and RTO_Code was returned. */
    val isFastlaneRto: Boolean = false,
    val isFastlaneManufacture: Boolean = false,
    val isFastlaneRegistration: Boolean = false
)

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
