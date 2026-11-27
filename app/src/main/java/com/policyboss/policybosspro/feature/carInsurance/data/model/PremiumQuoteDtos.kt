package com.policyboss.policybosspro.feature.carInsurance.data.model

import com.policyboss.policybosspro.core.network.NetworkConstants
import com.policyboss.policybosspro.core.serializer.LenientNullableStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/*
 * DTOs for the quote APIs that run after the vahan lookup:
 *   premium_initiate  -> everything except the echoed "Request" block
 *   premium_summary   -> the full response, including the echoed "Request" block
 *   premium_list_db   -> the full response, including Summary.Request_Core / Request_Product
 *
 * Fixed fields are typed. Master.Vehicle / Master.Rto are typed too, with their
 * `Insurer_<id>` entries grouped by insurer id (see PremiumQuoteMasterDtos.kt). Blocks
 * whose keys are dynamic (premium breakup maps) or whose values change type between insurers (Request_Core,
 * LM_Custom_Request) are kept as JSON, so nothing is dropped and a type change cannot
 * break parsing. Strings use LenientNullableStringSerializer because the backend
 * sometimes sends numbers, blanks or "null" in text fields.
 */

/**
 * Body for `POST quote/premium_summary` and `POST quote/premium_list_db`.
 * [searchReferenceNumber] is `Summary.Request_Unique_Id` from `premium_initiate`.
 */
@Serializable
data class PremiumSearchRequest(
    @SerialName("search_reference_number") val searchReferenceNumber: String,
    @SerialName("secret_key") val secretKey: String = NetworkConstants.SECRET_KEY,
    @SerialName("client_key") val clientKey: String = NetworkConstants.CLIENT_KEY
)

// =====================================================================
// premium_initiate (POST quote/premium_initiate) — "Request" is not mapped
// =====================================================================

@Serializable
data class PremiumInitiateResponseDto(
    @SerialName("Summary") val summary: PremiumInitiateSummaryDto? = null,
    @SerialName("Addon_Request") val addonRequest: JsonElement? = null,
    @SerialName("Master") val master: PremiumMasterDto? = null
)

@Serializable
data class PremiumInitiateSummaryDto(
    /** Search reference for premium_summary / premium_list_db. */
    @SerialName("Request_Unique_Id")
    @Serializable(with = LenientNullableStringSerializer::class)
    val requestUniqueId: String? = null,
    @SerialName("customer_reference_number")
    @Serializable(with = LenientNullableStringSerializer::class)
    val customerReferenceNumber: String? = null,
    @SerialName("Crn_Unique_Id")
    @Serializable(with = LenientNullableStringSerializer::class)
    val crnUniqueId: String? = null,
    @SerialName("crn") val crn: Long? = null,
    /** Website quotes page, relative to policyboss.com. */
    @SerialName("quote_url")
    @Serializable(with = LenientNullableStringSerializer::class)
    val quoteUrl: String? = null
)

// =====================================================================
// premium_summary (POST quote/premium_summary) — full response
// =====================================================================

@Serializable
data class PremiumSummaryResponseDto(
    /** Cache status, e.g. `Not_Cached`; the search status is [PremiumSummaryDto.status]. */
    @SerialName("Status")
    @Serializable(with = LenientNullableStringSerializer::class)
    val status: String? = null,
    @SerialName("Summary") val summary: PremiumSummaryDto? = null,
    /** The quote request as the backend stored it (vehicle, RTO, policy, RM / POSP details). */
    @SerialName("Request") val request: PremiumSummaryRequestDto? = null,
    @SerialName("Addon_Request") val addonRequest: JsonElement? = null,
    @SerialName("Master") val master: PremiumMasterDto? = null
)

@Serializable
data class PremiumSummaryDto(
    @SerialName("Request_Unique_Id")
    @Serializable(with = LenientNullableStringSerializer::class)
    val requestUniqueId: String? = null,
    @SerialName("customer_reference_number")
    @Serializable(with = LenientNullableStringSerializer::class)
    val customerReferenceNumber: String? = null,
    @SerialName("User_Data_Id") val userDataId: Long? = null,
    @SerialName("Created_On")
    @Serializable(with = LenientNullableStringSerializer::class)
    val createdOn: String? = null,
    @SerialName("PB_CRN") val pbCrn: Long? = null,
    @SerialName("Client_Id") val clientId: Int? = null,
    @SerialName("Request_Id") val requestId: Long? = null,
    /** `complete` once every insurer has answered. */
    @SerialName("Status")
    @Serializable(with = LenientNullableStringSerializer::class)
    val status: String? = null,
    @SerialName("Total") val total: Int? = null,
    @SerialName("Pending") val pending: Int? = null,
    @SerialName("Complete") val complete: Int? = null,
    @SerialName("Success") val success: Int? = null,
    @SerialName("Fail") val fail: Int? = null,
    @SerialName("Total_Execution_Time") val totalExecutionTime: Double? = null
)

// =====================================================================
// premium_list_db (POST quote/premium_list_db) — full response
// =====================================================================

@Serializable
data class PremiumListResponseDto(
    @SerialName("Error") val error: JsonElement? = null,
    @SerialName("Summary") val summary: PremiumListSummaryDto? = null,
    /** One entry per insurer, with its plans or its error. */
    @SerialName("Response") val response: List<InsurerPremiumDto> = emptyList(),
    @SerialName("Zextra_Response") val zextraResponse: JsonElement? = null,
    @SerialName("Addon_Request") val addonRequest: JsonElement? = null,
    /** Error code -> description, e.g. `LM009` -> `Vehicle Underwriting Restriction`. */
    @SerialName("Error_Master") val errorMaster: Map<String, String> = emptyMap()
)

@Serializable
data class PremiumListSummaryDto(
    @SerialName("_id")
    @Serializable(with = LenientNullableStringSerializer::class)
    val id: String? = null,
    @SerialName("Request_Id") val requestId: Long? = null,
    @SerialName("Request_Unique_Id")
    @Serializable(with = LenientNullableStringSerializer::class)
    val requestUniqueId: String? = null,
    @SerialName("Client_Id") val clientId: Int? = null,
    @SerialName("PB_CRN") val pbCrn: Long? = null,
    /** The full quote request as stored by the backend (~150 fields, mixed types). */
    @SerialName("Request_Core") val requestCore: JsonObject? = null,
    /** The insurer-facing request: policy dates, add-on flags, NCB, vehicle age. */
    @SerialName("Request_Product") val requestProduct: JsonObject? = null,
    @SerialName("Created_On")
    @Serializable(with = LenientNullableStringSerializer::class)
    val createdOn: String? = null,
    @SerialName("Status")
    @Serializable(with = LenientNullableStringSerializer::class)
    val status: String? = null,
    @SerialName("Total") val total: Int? = null,
    @SerialName("Pending") val pending: Int? = null,
    @SerialName("Complete") val complete: Int? = null,
    @SerialName("Success") val success: Int? = null,
    @SerialName("Fail") val fail: Int? = null,
    @SerialName("Total_Execution_Time") val totalExecutionTime: Double? = null,
    @SerialName("__v") val version: Int? = null,
    @SerialName("Inprogress_Insurer") val inProgressInsurer: JsonElement? = null,
    @SerialName("Request_Unique_Id_Core")
    @Serializable(with = LenientNullableStringSerializer::class)
    val requestUniqueIdCore: String? = null,
    @SerialName("vehicle_min_idv") val vehicleMinIdv: Double? = null,
    @SerialName("vehicle_max_idv") val vehicleMaxIdv: Double? = null,
    @SerialName("Actual_Time") val actualTime: Double? = null,
    @SerialName("Idv_Min") val idvMin: Double? = null,
    @SerialName("Idv_Max") val idvMax: Double? = null,
    @SerialName("Premium_Min") val premiumMin: Double? = null,
    @SerialName("Premium_Max") val premiumMax: Double? = null,
    @SerialName("Insurer_Cnt") val insurerCount: Int? = null,
    @SerialName("Insurer_Success_Count") val insurerSuccessCount: Int? = null,
    @SerialName("Plan_Success_Count") val planSuccessCount: Int? = null,
    @SerialName("Common_Addon") val commonAddon: JsonElement? = null,
    @SerialName("Last_Status")
    @Serializable(with = LenientNullableStringSerializer::class)
    val lastStatus: String? = null,
    @SerialName("Prefered_Insurer_list") val preferredInsurerIds: List<Int> = emptyList()
)

@Serializable
data class InsurerPremiumDto(
    @SerialName("Service_Log_Id") val serviceLogId: Long? = null,
    @SerialName("Service_Log_Unique_Id_Core")
    @Serializable(with = LenientNullableStringSerializer::class)
    val serviceLogUniqueIdCore: String? = null,
    @SerialName("Service_Log_Unique_Id")
    @Serializable(with = LenientNullableStringSerializer::class)
    val serviceLogUniqueId: String? = null,
    @SerialName("Insurer_Id") val insurerId: Int? = null,
    @SerialName("Insurer") val insurer: InsurerInfoDto? = null,
    @SerialName("Premium_Breakup") val premiumBreakup: PremiumBreakupDto? = null,
    @SerialName("Premium_Rate") val premiumRate: PremiumRateDto? = null,
    @SerialName("Addon_List") val addonList: JsonElement? = null,
    /** `BUFFET` or `ALACARTE`. */
    @SerialName("Addon_Mode")
    @Serializable(with = LenientNullableStringSerializer::class)
    val addonMode: String? = null,
    @SerialName("Plan_List") val planList: List<InsurerPlanDto> = emptyList(),
    /** Insurer master mapping (dbmaster_*); values mix int / string / blank, so kept raw. */
    @SerialName("LM_Custom_Request") val customRequest: Map<String, JsonElement>? = null,
    @SerialName("Completion_Summary") val completionSummary: CompletionSummaryDto? = null,
    @SerialName("Error_Code")
    @Serializable(with = LenientNullableStringSerializer::class)
    val errorCode: String? = null,
    @SerialName("Error_Msg")
    @Serializable(with = LenientNullableStringSerializer::class)
    val errorMessage: String? = null,
    @SerialName("Error_Specific")
    @Serializable(with = LenientNullableStringSerializer::class)
    val errorSpecific: String? = null,
    @SerialName("Is_Special_Price") val isSpecialPrice: Boolean? = null
)

@Serializable
data class InsurerInfoDto(
    @SerialName("_id")
    @Serializable(with = LenientNullableStringSerializer::class)
    val id: String? = null,
    @SerialName("CreatedOn")
    @Serializable(with = LenientNullableStringSerializer::class)
    val createdOn: String? = null,
    @SerialName("Insurer_Code")
    @Serializable(with = LenientNullableStringSerializer::class)
    val code: String? = null,
    @SerialName("Insurer_ID") val insurerId: Int? = null,
    @SerialName("Insurer_Logo_Name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val logoName: String? = null,
    @SerialName("Insurer_Logo_Name_Mobile")
    @Serializable(with = LenientNullableStringSerializer::class)
    val logoNameMobile: String? = null,
    @SerialName("Insurer_Name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val name: String? = null,
    @SerialName("IsActive") val isActive: Int? = null,
    @SerialName("IsInternal")
    @Serializable(with = LenientNullableStringSerializer::class)
    val isInternal: String? = null,
    @SerialName("PreviousInsurer_Address")
    @Serializable(with = LenientNullableStringSerializer::class)
    val address: String? = null,
    @SerialName("PreviousInsurer_City")
    @Serializable(with = LenientNullableStringSerializer::class)
    val city: String? = null,
    /** Number for most insurers, string for some. */
    @SerialName("PreviousInsurer_Pincode")
    @Serializable(with = LenientNullableStringSerializer::class)
    val pincode: String? = null,
    @SerialName("PreviousInsurer_State")
    @Serializable(with = LenientNullableStringSerializer::class)
    val state: String? = null,
    @SerialName("Insurer_Type")
    @Serializable(with = LenientNullableStringSerializer::class)
    val insurerType: String? = null,
    @SerialName("Product_Id") val productIds: List<Int> = emptyList(),
    /** Full logo URL (the plain `Insurer_Logo_Name` is only a file name). */
    @SerialName("Insurer_Logo_Name_Whatsapp")
    @Serializable(with = LenientNullableStringSerializer::class)
    val logoUrl: String? = null
)

@Serializable
data class InsurerPlanDto(
    @SerialName("Plan_Id") val planId: Int? = null,
    @SerialName("Plan_Name")
    @Serializable(with = LenientNullableStringSerializer::class)
    val planName: String? = null,
    @SerialName("Service_Log_Id") val serviceLogId: Long? = null,
    @SerialName("Service_Log_Unique_Id_Core")
    @Serializable(with = LenientNullableStringSerializer::class)
    val serviceLogUniqueIdCore: String? = null,
    @SerialName("Service_Log_Unique_Id")
    @Serializable(with = LenientNullableStringSerializer::class)
    val serviceLogUniqueId: String? = null,
    @SerialName("Insurer_Transaction_Identifier")
    @Serializable(with = LenientNullableStringSerializer::class)
    val insurerTransactionIdentifier: String? = null,
    /** Add-on code -> premium; empty for TP-only plans. */
    @SerialName("Plan_Addon_Breakup") val addonBreakup: Map<String, JsonElement> = emptyMap(),
    @SerialName("Plan_Addon_Premium") val addonPremium: Double? = null,
    @SerialName("Full_Premium_Breakup") val fullPremiumBreakup: PremiumBreakupDto? = null,
    @SerialName("Proposal_Url")
    @Serializable(with = LenientNullableStringSerializer::class)
    val proposalUrl: String? = null
)

/**
 * Premium split. `own_damage` / `liability` hold insurer-specific keys (some blank,
 * some null), so they are kept as maps.
 */
@Serializable
data class PremiumBreakupDto(
    @SerialName("own_damage") val ownDamage: Map<String, JsonElement> = emptyMap(),
    @SerialName("liability") val liability: Map<String, JsonElement> = emptyMap(),
    @SerialName("rsa_cpa_bundle_breakup") val rsaCpaBundle: RsaCpaBundleDto? = null,
    @SerialName("net_premium") val netPremium: Double? = null,
    @SerialName("service_tax") val serviceTax: Double? = null,
    @SerialName("final_premium") val finalPremium: Double? = null,
    @SerialName("DIGIT_PAYD") val digitPayd: JsonElement? = null,
    /** `0` or an insurer quote id string. */
    @SerialName("QUOTE_ID") val quoteId: JsonElement? = null,
    @SerialName("RELIANCE_GARAGE") val relianceGarage: JsonElement? = null
)

@Serializable
data class PremiumRateDto(
    @SerialName("own_damage") val ownDamage: Map<String, JsonElement> = emptyMap(),
    @SerialName("liability") val liability: Map<String, JsonElement> = emptyMap(),
    @SerialName("addon") val addon: Map<String, JsonElement> = emptyMap(),
    @SerialName("rsa_cpa_bundle_breakup") val rsaCpaBundle: RsaCpaBundleDto? = null
)

@Serializable
data class RsaCpaBundleDto(
    @SerialName("bundle_net_premium") val netPremium: Double? = null,
    @SerialName("bundle_final_premium") val finalPremium: Double? = null,
    @SerialName("bundle_service_tax") val serviceTax: Double? = null
)

@Serializable
data class CompletionSummaryDto(
    @SerialName("Total") val total: Int? = null,
    @SerialName("Completed") val completed: Int? = null,
    @SerialName("Status")
    @Serializable(with = LenientNullableStringSerializer::class)
    val status: String? = null
)
