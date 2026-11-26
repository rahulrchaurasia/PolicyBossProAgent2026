package com.policyboss.policybosspro.feature.carInsurance.data.mapper

import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumInitiateRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumInitiateResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumInitiateRequest.Companion.NO
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumInitiateRequest.Companion.YES
import com.policyboss.policybosspro.feature.carInsurance.model.OwnershipType
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumInitiateInput
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumInitiateResult
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.Locale

// =====================================================================
// premium_initiate: input -> request
// =====================================================================

/**
 * Builds the `quote/premium_initiate` body from the vahan lookup ([PremiumInitiateInput.vehicle])
 * and the form choices. Pure: [today] is passed in so the expiry-date rules are testable.
 */
fun buildPremiumInitiateRequest(
    input: PremiumInitiateInput,
    ssId: String,
    appVersion: String,
    today: LocalDate
): PremiumInitiateRequest {
    val vehicle = input.vehicle
    val premium = vehicle.premiumData
    val name = splitCustomerName(input.customerName)
    val registrationNo = formatRegistrationNumber(vehicle.registrationNumber)
    val ccRange = vahanCcRange(premium.vahanCcSlab)

    // The form field is pre-filled from Pyp_Policy_Expiry_Date and may have been edited
    val expiryDate = VehicleDateFormatter.toApiDate(input.policyExpiryDate)
    // Break-in: previous policy expiry is before today
    val isBreakin = isPolicyExpired(expiryDate, today).yesNo()

    return PremiumInitiateRequest(
        vehicleId = vehicle.variantId.toIntOrZero(),
        rtoId = vehicle.rtoCode.toIntOrZero(),
        vehicleRegistrationDate = premium.registrationDateApi
            .ifBlank { VehicleDateFormatter.toApiDate(vehicle.registrationDate) },
        vehicleManfDate = premium.manufactureDateApi
            .ifBlank { manufactureApiDate(null, vehicle.manufacturingMonth, vehicle.manufacturingYear) },
        policyExpiryDate = expiryDate,
        prevInsurerId = input.previousInsurerId.toIntOrZero(),
        vehicleRegistrationType = when (input.ownershipType) {
            OwnershipType.INDIVIDUAL -> "individual"
            OwnershipType.COMPANY -> "corporate"
        },
        vehicleNcbCurrent = input.ncbPercent.filter { it.isDigit() }.ifBlank { "0" },
        isClaimExists = input.hasClaim.yesNo(),
        isFastlaneRto = premium.isFastlaneRto.yesNo(),
        registrationNo = registrationNo,
        regnoRtoCode = registrationNo.split('-').take(2).takeIf { it.size == 2 }?.joinToString("-").orEmpty(),
        fuelType = vehicle.fuelType,
        firstName = name.first,
        middleName = name.middle,
        lastName = name.last,
        ssId = ssId.ifBlank { "0" },
        appVersion = appVersion,
        vehicleInsuranceSubtype = input.policyType.apiSubtype,
        isBreakin = isBreakin,
        // Expiry date is only filled when "Do you have existing policy?" is Yes
        isPolicyExist = expiryDate.isNotBlank().yesNo(),
        chassisNumber = premium.chassisNumber,
        enginNumber = premium.engineNumber,
        isFastlaneManufacture = premium.isFastlaneManufacture.yesNo(),
        isFastlaneRegistration = premium.isFastlaneRegistration.yesNo(),
        tpInsurerId = input.tpInsurerId,
        customerName = input.customerName.trim(),
        permanentPincode = premium.permanentPincode,
        previousPolicyNumber = premium.previousPolicyNumber,
        vahanCcMin = ccRange.first,
        vahanCcMax = ccRange.second,
        vahanCcNormal = premium.vahanCubicCapacity.toIntOrZero(),
        vahanCcSlab = premium.vahanCcSlab,
        vahanSource = premium.vahanSource,
        vahanFull = premium.vahanFull,
        matchVehicleFull = premium.matchVehicleFull
    )
}

/** Name parts for `first_name` / `middle_name` / `last_name`. */
data class CustomerNameParts(val first: String, val middle: String, val last: String)

/**
 * `VIJAYKUMAR` -> (VIJAYKUMAR, "", ""); `VIJAYKUMAR PAWAR` -> (VIJAYKUMAR, "", PAWAR);
 * `VIJAYKUMAR J PAWAR` -> (VIJAYKUMAR, J, PAWAR). Inner words all go to middle.
 */
fun splitCustomerName(fullName: String): CustomerNameParts {
    val words = fullName.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when (words.size) {
        0 -> CustomerNameParts("", "", "")
        1 -> CustomerNameParts(words[0], "", "")
        2 -> CustomerNameParts(words[0], "", words[1])
        else -> CustomerNameParts(words.first(), words.subList(1, words.lastIndex).joinToString(" "), words.last())
    }
}

/** `MH01BB0197` -> `MH-01-BB-0197`; unrecognised formats are returned compacted. */
fun formatRegistrationNumber(raw: String): String {
    val compact = raw.compactRegistration()
    val match = Regex("^([A-Z]{2})(\\d{1,2})([A-Z]{0,3})(\\d{1,4})$").find(compact) ?: return compact
    val (state, district, series, number) = match.destructured
    return listOf(state, district.padStart(2, '0'), series, number).filter { it.isNotBlank() }.joinToString("-")
}

/**
 * `vahan_cc_min` / `vahan_cc_max` for an IRDAI private-car TP slab:
 * `0_TO_1000` -> 600..1000, `1000_TO_1500` -> 1000..1500, `1500_ABOVE` -> 1500..0 (no upper bound).
 * Any other `<min>_TO_<max>` slab is parsed as-is; unknown -> 0..0.
 */
fun vahanCcRange(slab: String): Pair<Int, Int> {
    val value = slab.trim().uppercase(Locale.ENGLISH)
    return when (value) {
        "0_TO_1000" -> 600 to 1000
        "1000_TO_1500" -> 1000 to 1500
        "1500_ABOVE" -> 1500 to 0 // TODO confirm upper bound with backend
        else -> Regex("^(\\d+)_TO_(\\d+)$").find(value)
            ?.let { it.groupValues[1].toInt() to it.groupValues[2].toInt() }
            ?: (0 to 0)
    }
}

/** True when [expiryDateApi] (`yyyy-MM-dd`) is before [today]; blank/invalid -> false. */
fun isPolicyExpired(expiryDateApi: String, today: LocalDate): Boolean {
    if (expiryDateApi.isBlank()) return false
    return try {
        LocalDate.parse(expiryDateApi).isBefore(today)
    } catch (_: DateTimeParseException) {
        false
    }
}

private fun Boolean.yesNo(): String = if (this) YES else NO

private fun String.toIntOrZero(): Int = trim().toIntOrNull() ?: trim().toDoubleOrNull()?.toInt() ?: 0

// =====================================================================
// premium_initiate: response -> result
// =====================================================================

/**
 * Keeps only the search reference and CRN. Returns null without a `Request_Unique_Id`,
 * since premium_summary / premium_list_db cannot be called without it.
 */
fun PremiumInitiateResponseDto.toPremiumInitiateResult(): PremiumInitiateResult? {
    val searchReference = summary?.requestUniqueId?.trim().orEmpty()
    if (searchReference.isBlank()) return null
    return PremiumInitiateResult(
        crn = summary?.crn?.takeIf { it > 0 }?.toString().orEmpty(),
        requestUniqueId = searchReference
    )
}
