package com.policyboss.policybosspro.feature.carInsurance.model

import androidx.compose.runtime.Immutable

/**
 * The form values `quote/premium_initiate` needs on top of the vahan lookup.
 * Built from [NewCarUiState] by [NewCarUiState.toPremiumInitiateInput].
 */
@Immutable
data class PremiumInitiateInput(
    val vehicle: VehicleInfoDomain,
    val customerName: String,
    val ownershipType: OwnershipType,
    val policyType: PolicyType,
    /** NCB percent without the sign: `0`, `20`, `25` ... */
    val ncbPercent: String,
    /** Previous-policy expiry as shown on the form (`dd-MMM-yyyy`); blank when unknown. */
    val policyExpiryDate: String,
    val previousInsurerId: String,
    val tpInsurerId: String,
    /**
     * Value for `is_claim_exists`: the "made any claim" radio for OD / Comprehensive,
     * always true for TP Only, false when there is no existing policy.
     */
    val hasClaim: Boolean = false
)

fun NewCarUiState.toPremiumInitiateInput(vehicle: VehicleInfoDomain): PremiumInitiateInput =
    PremiumInitiateInput(
        vehicle = vehicle,
        customerName = customerName.trim().ifBlank { vehicle.ownerName },
        ownershipType = ownershipType,
        policyType = policyType,
        ncbPercent = if (shouldShowClaimAndNcb) selectedNcb?.value.orEmpty().ifBlank { "0" } else "0",
        policyExpiryDate = if (shouldShowExpiryDate) policyExpiryDate else "",
        previousInsurerId = selectedInsurer?.value.orEmpty().ifBlank { vehicle.previousInsurerId },
        tpInsurerId = if (shouldShowTpInsurer) selectedTpInsurer?.value.orEmpty() else "",
        hasClaim = when {
            !shouldShowExpiryDate -> false // no existing policy (or new car)
            policyType == PolicyType.TP_ONLY -> true
            else -> shouldShowClaimAndNcb && hasClaim
        }
    )

/**
 * What the app keeps from `quote/premium_initiate`: the search reference for the follow-up
 * quote APIs and the CRN shown on the results screen.
 */
@Immutable
data class PremiumInitiateResult(
    val crn: String,
    val requestUniqueId: String
)
