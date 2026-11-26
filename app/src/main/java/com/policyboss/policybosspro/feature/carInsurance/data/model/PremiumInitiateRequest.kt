package com.policyboss.policybosspro.feature.carInsurance.data.model

import com.policyboss.policybosspro.core.network.NetworkConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request body for `POST quote/premium_initiate`.
 *
 * Vehicle values come from the `quote/vehicle_info_user` lookup, user choices from the form;
 * everything else is a fixed default. JSON types (string vs number) match the backend contract.
 * Built by `buildPremiumInitiateRequest` in `PremiumInitiateRequestMapper.kt`.
 */
@Serializable
data class PremiumInitiateRequest(
    @SerialName("product_id") val productId: Int = VehicleInfoRequest.CAR_PRODUCT_ID,
    @SerialName("vehicle_id") val vehicleId: Int,
    @SerialName("rto_id") val rtoId: Int,
    @SerialName("vehicle_insurance_type") val vehicleInsuranceType: String = "renew",
    @SerialName("vehicle_registration_date") val vehicleRegistrationDate: String,
    @SerialName("vehicle_manf_date") val vehicleManfDate: String,
    @SerialName("policy_expiry_date") val policyExpiryDate: String,
    @SerialName("prev_insurer_id") val prevInsurerId: Int,
    @SerialName("vehicle_registration_type") val vehicleRegistrationType: String,
    @SerialName("vehicle_ncb_current") val vehicleNcbCurrent: String,
    @SerialName("is_claim_exists") val isClaimExists: String,
    @SerialName("is_ownership_transfer") val isOwnershipTransfer: String = NO,
    @SerialName("method_type") val methodType: String = "Premium",
    @SerialName("execution_async") val executionAsync: String = YES,
    @SerialName("electrical_accessory") val electricalAccessory: Int = 0,
    @SerialName("non_electrical_accessory") val nonElectricalAccessory: Int = 0,
    @SerialName("is_fastlane_rto") val isFastlaneRto: String,
    @SerialName("registration_no") val registrationNo: String,
    @SerialName("regno_rtocode") val regnoRtoCode: String,
    @SerialName("is_llpd") val isLlpd: String = NO,
    @SerialName("is_antitheft_fit") val isAntitheftFit: String = NO,
    @SerialName("is_external_bifuel") val isExternalBifuel: String = NO,
    @SerialName("is_aai_member") val isAaiMember: String = NO,
    @SerialName("fuel_type") val fuelType: String,
    @SerialName("external_bifuel_type") val externalBifuelType: String = "",
    @SerialName("external_bifuel_value") val externalBifuelValue: String = "0",
    @SerialName("pa_owner_driver_si") val paOwnerDriverSi: Int = 1_500_000,
    @SerialName("is_pa_od") val isPaOd: String = NO,
    @SerialName("is_having_valid_dl") val isHavingValidDl: String = NO,
    @SerialName("is_opted_standalone_cpa") val isOptedStandaloneCpa: String = YES,
    @SerialName("pa_paid_driver_si") val paPaidDriverSi: String = "0",
    @SerialName("first_name") val firstName: String,
    @SerialName("middle_name") val middleName: String,
    @SerialName("mobile") val mobile: String = "9999999999",
    @SerialName("email") val email: String = "abc@gmail.com",
    @SerialName("crn") val crn: Int = 0,
    @SerialName("ss_id") val ssId: String,
    @SerialName("sub_fba_id") val subFbaId: String = "0",
    @SerialName("client_id") val clientId: Int = 2,
    @SerialName("fba_id") val fbaId: String = "0",
    @SerialName("geo_lat") val geoLat: Int = 0,
    @SerialName("geo_long") val geoLong: Int = 0,
    @SerialName("agent_source") val agentSource: String = "",
    @SerialName("app_version") val appVersion: String,
    @SerialName("vehicle_insurance_subtype") val vehicleInsuranceSubtype: String,
    @SerialName("secret_key") val secretKey: String = NetworkConstants.SECRET_KEY,
    @SerialName("client_key") val clientKey: String = NetworkConstants.CLIENT_KEY,
    @SerialName("is_breakin") val isBreakin: String,
    @SerialName("is_inspection_done") val isInspectionDone: String = NO,
    @SerialName("is_policy_exist") val isPolicyExist: String,
    @SerialName("client_name") val clientName: String = "PolicyBoss",
    @SerialName("udid") val udid: String = "",
    @SerialName("last_name") val lastName: String,
    @SerialName("ui_source") val uiSource: String = "UI24",
    @SerialName("is_financed") val isFinanced: String = NO,
    @SerialName("is_oslc") val isOslc: String = NO,
    @SerialName("oslc_si") val oslcSi: Int = 0,
    @SerialName("ip_address") val ipAddress: String = "",
    @SerialName("ip_city_state") val ipCityState: String = "",
    @SerialName("is_helmet_cover") val isHelmetCover: String = NO,
    @SerialName("helmet_cover_si") val helmetCoverSi: Int = 0,
    @SerialName("search_from") val searchFrom: String = "diy",
    @SerialName("is_quick_quote") val isQuickQuote: String = NO,
    @SerialName("chassis_number") val chassisNumber: String,
    @SerialName("engin_number") val enginNumber: String,
    @SerialName("is_fastlane_manufacture") val isFastlaneManufacture: String,
    @SerialName("is_fastlane_registration") val isFastlaneRegistration: String,
    @SerialName("visitor_id") val visitorId: String = "",
    @SerialName("addon_applied") val addonApplied: String = NO,
    @SerialName("vehicle_expected_idv") val vehicleExpectedIdv: Int = 0,
    @SerialName("pa_unnamed_passenger_si") val paUnnamedPassengerSi: String = "0",
    @SerialName("voluntary_deductible") val voluntaryDeductible: String = "0",
    @SerialName("is_tppd") val isTppd: String = NO,
    @SerialName("pucc_end_date") val puccEndDate: String = "",
    @SerialName("pucc_number") val puccNumber: String = "",
    @SerialName("tp_insurer_id") val tpInsurerId: String,
    @SerialName("customer_name") val customerName: String,
    @SerialName("permanent_pincode") val permanentPincode: String,
    @SerialName("previous_policy_number") val previousPolicyNumber: String,
    @SerialName("vahan_cc_min") val vahanCcMin: Int,
    @SerialName("vahan_cc_max") val vahanCcMax: Int,
    @SerialName("vahan_cc_normal") val vahanCcNormal: Int,
    @SerialName("vahan_cc_slab") val vahanCcSlab: String,
    @SerialName("vahan_source") val vahanSource: String,
    @SerialName("vahan_full") val vahanFull: String,
    @SerialName("match_vehicle_full") val matchVehicleFull: String
) {
    companion object {
        const val YES = "yes"
        const val NO = "no"
    }
}
