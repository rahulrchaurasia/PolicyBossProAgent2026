package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model

import androidx.compose.runtime.Immutable

enum class FilterCategory(val displayName: String) {
    SET_IDV("Set IDV"),
    ADD_ONS("Add-ons"),
    ADDITIONAL_COVER("Additional Cover"),
    DISCOUNTS("Discounts")
}

enum class ZeroDepClaimLimit(val displayName: String, val shortLabel: String) {
    ONE_CLAIM("1 Claim", "1"),
    TWO_CLAIMS("2 Claim(s)", "2"),
    UNLIMITED_CLAIMS("Unlimited Claim(s)", "Unlimited")
}

@Immutable
data class AddOnFilterItem(
    val id: String,
    val title: String,
    val description: String,
    val category: String
)

@Immutable
data class LiabilityCoverItem(
    val id: String,
    val title: String,
    val minBound: Float = 100f,
    val maxBound: Float = 5000f
)

@Immutable
data class InfoCalloutItem(
    val title: String,
    val description: String
)

@Immutable
data class QuoteFilterState(
    // Set IDV
    val selectedIdv: Float = 71414f,
    val defaultIdv: Float = 71414f,
    val minIdv: Float = 71414f,
    val maxIdv: Float = 234780f,
    val isIdvExplicitlyChanged: Boolean = false,

    // Add-ons
    val selectedAddOnIds: Set<String> = setOf("zero_dep"),
    val expandedAddOnIds: Set<String> = setOf("zero_dep"),
    val selectedZeroDepClaimLimit: ZeroDepClaimLimit = ZeroDepClaimLimit.ONE_CLAIM,

    // Liability Covers / Additional Cover
    val selectedLiabilityCoverIds: Set<String> = setOf("owner_driver_pa"),
    val liabilityCoverRanges: Map<String, ClosedFloatingPointRange<Float>> = mapOf(
        "owner_driver_pa" to 300f..4000f,
        "paid_driver_liability" to 300f..4000f,
        "paid_driver_pa" to 300f..4000f,
        "limited_tp_damage" to 300f..4000f
    ),
    val selectedUnNamedPassengerCover: String? = "No",
    val electricalAccessoriesAmount: String = "0",
    val nonElectricalAccessoriesAmount: String = "0",

    // Discounts
    val antiTheftDevice: Boolean = true,
    val payAsYouDrive: Boolean = true,
    val voluntaryExcess: String = "No"
) {
    val electricalAmountInt: Int
        get() = electricalAccessoriesAmount.filter { it.isDigit() }.toIntOrNull() ?: 0

    val isElectricalValid: Boolean
        get() {
            val amount = electricalAmountInt
            return amount == 0 || amount in 10000..50000
        }

    val isElectricalActive: Boolean
        get() = electricalAmountInt in 10000..50000

    val nonElectricalAmountInt: Int
        get() = nonElectricalAccessoriesAmount.filter { it.isDigit() }.toIntOrNull() ?: 0

    val isNonElectricalValid: Boolean
        get() {
            val amount = nonElectricalAmountInt
            return amount == 0 || amount in 10000..50000
        }

    val isNonElectricalActive: Boolean
        get() = nonElectricalAmountInt in 10000..50000

    val isAccessoriesValid: Boolean
        get() = isElectricalValid && isNonElectricalValid

    val isIdvActive: Boolean
        get() = isIdvExplicitlyChanged || (selectedIdv != defaultIdv)

    /** Filters visible in TP-only mode: liability covers and un-named passenger cover. */
    val additionalCoverFilterCount: Int
        get() = selectedLiabilityCoverIds.size +
            if (!selectedUnNamedPassengerCover.isNullOrBlank() && selectedUnNamedPassengerCover != "No") 1 else 0

    /** Count for the Apply button; TP-only counts just the Additional Cover filters it shows. */
    fun activeFilterCount(additionalCoverOnly: Boolean): Int =
        if (additionalCoverOnly) additionalCoverFilterCount else activeFilterCount

    /** Additional Cover cleared, every other category untouched (Reset in TP-only mode). */
    fun resetAdditionalCover(isCompany: Boolean = false): QuoteFilterState = copy(
        selectedLiabilityCoverIds = emptySet(),
        selectedUnNamedPassengerCover = "No",
        electricalAccessoriesAmount = "0",
        nonElectricalAccessoriesAmount = "0"
    )

    val isOwnerDriverPaCover: Boolean
        get() = selectedLiabilityCoverIds.contains("owner_driver_pa")

    fun sanitizeForOwnership(isCompany: Boolean): QuoteFilterState =
        if (isCompany && selectedLiabilityCoverIds.contains("owner_driver_pa")) {
            copy(selectedLiabilityCoverIds = selectedLiabilityCoverIds - "owner_driver_pa")
        } else {
            this
        }

    val activeFilterCount: Int
        get() {
            var count = 0
            if (isIdvActive) count++
            count += selectedAddOnIds.size
            count += selectedLiabilityCoverIds.size
            if (!selectedUnNamedPassengerCover.isNullOrBlank() && selectedUnNamedPassengerCover != "No") count++
            if (isElectricalActive) count++
            if (isNonElectricalActive) count++
            if (antiTheftDevice) count++
            if (payAsYouDrive) count++
            if (voluntaryExcess != "No") count++
            return count
        }
}

object MotorQuoteFilterData {
    val addOnItems = listOf(
        AddOnFilterItem(
            id = "zero_dep",
            title = "Zero Depreciation",
            description = "Pays the full repair cost—no depreciation cuts during claims",
            category = "Repairs & Wear"
        ),
        AddOnFilterItem(
            id = "rsa",
            title = "RoadSide Assistance",
            description = "24x7 emergency roadside assistance, towing, and flat tyre service.",
            category = "Repairs & Wear"
        ),
        AddOnFilterItem(
            id = "engine_protection",
            title = "Engine Protection",
            description = "Covers damage to internal engine parts caused by lubrication failure or water ingress.",
            category = "Engine/Mechanical"
        ),
        AddOnFilterItem(
            id = "consumables",
            title = "Consumables",
            description = "Covers consumable items like engine oil, nuts, bolts during repairs.",
            category = "Repairs & Wear"
        ),
        AddOnFilterItem(
            id = "key_cover",
            title = "Key Cover",
            description = "Reimburses cost of key replacement and locksmith charges.",
            category = "Protection"
        ),
        AddOnFilterItem(
            id = "tyre_cover",
            title = "Tyre Cover",
            description = "Covers accidental tyre damage, puncture, and replacement.",
            category = "Protection"
        ),
        AddOnFilterItem(
            id = "ncb_protect",
            title = "NCB Protect",
            description = "Protects your No Claim Bonus discount even after a claim.",
            category = "Discount Protection"
        ),
        AddOnFilterItem(
            id = "invoice_cover",
            title = "Invoice Cover",
            description = "Return to invoice cover pays the original car invoice value upon total loss.",
            category = "Protection"
        ),
        AddOnFilterItem(
            id = "repair_cost_saver",
            title = "Repair Cost Saver",
            description = "Covers consumable items like engine oil, nuts, bolts during repairs.",
            category = "Repairs & Wear"
        ),
        AddOnFilterItem(
            id = "water_impact",
            title = "Water-Impact Protection",
            description = "Protects against water ingress causing hydrostatic lock in the engine.",
            category = "Repairs & Wear"
        ),
        AddOnFilterItem(
            id = "gearbox_safety",
            title = "Gearbox Safety Cover",
            description = "Covers repair/replacement of gearbox parts damaged due to oil leakage or water ingress.",
            category = "Repairs & Wear"
        )
    )

    val liabilityCallouts = listOf(
        InfoCalloutItem(
            title = "Owner Driver PA Cover",
            description = "Financial cover for the owner-driver in case of accidental death or permanent disability."
        ),
        InfoCalloutItem(
            title = "Paid Driver Cover",
            description = "Protects your hired driver against accidental bodily injuries during duty."
        ),
        InfoCalloutItem(
            title = "Passenger Protection",
            description = "Safeguards all named or unnamed passengers riding in the insured vehicle."
        ),
        InfoCalloutItem(
            title = "Third Party Property Damage",
            description = "Limits or enhances liability coverage for damages caused to third party property."
        )
    )

    val additionalCoverItems = listOf(
        LiabilityCoverItem("owner_driver_pa", "Owner Driver PA Cover"),
        LiabilityCoverItem("paid_driver_liability", "Paid Driver Legal Liability"),
        LiabilityCoverItem("paid_driver_pa", "Paid Driver PA Cover"),
        LiabilityCoverItem("limited_tp_damage", "Limited 3rd Party Damage of 6,000")
    )

    val liabilityItems = additionalCoverItems

    val unNamedPassengerOptions = listOf("No", "₹ 50,000", "₹ 1,00,000", "₹ 2,00,000")

    val voluntaryExcessOptions = listOf("No", "2,500", "5,000", "7,500", "15,000")
}
