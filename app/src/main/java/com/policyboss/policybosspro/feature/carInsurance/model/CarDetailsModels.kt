package com.policyboss.policybosspro.feature.carInsurance.model

import androidx.compose.runtime.Immutable

/**
 * Stages in the unified Car Details selection workflow.
 * 1. [MAKE_MODEL]: Select combined Car Make & Model (e.g. HYUNDAI, CRETA).
 * 2. [FUEL]: Select supported Fuel Type (e.g. PETROL, DIESEL, CNG).
 * 3. [VARIANT]: Select Variant (e.g. 1.4 SX (O) DCT TURBO).
 */
enum class CarDetailsStep(val stepNumber: Int, val title: String) {
    MAKE_MODEL(1, "Select Car Brand"),
    FUEL(2, "Select Car Fuel Type"),
    VARIANT(3, "Select Car Variant")
}

/**
 * Direction of horizontal slide animations between catalogue stages.
 */
enum class NavigationDirection {
    FORWARD,
    BACKWARD
}

/**
 * Model representing combined Make & Model item displayed in 2-column card grid.
 */
@Immutable
data class CarBrandModelItem(
    val makeId: String,
    val makeName: String,
    val modelId: String,
    val modelName: String,
    val isPopular: Boolean = false,
    /** Full catalogue name from the live model list (e.g. "MARUTI WAGON R"); searchable. */
    val vehicleName: String = ""
) {
    val displayName: String = "$makeName, $modelName"
    val key: String = "${makeId}_${modelId}".uppercase()
}

/**
 * Model representing a specific Variant under a Make + Model + Fuel combination.
 */
@Immutable
data class CarVariantItem(
    /** Vehicle_ID from beta_GetFuelVariant (the id used for quotes). */
    val variantId: String,
    val variantName: String,
    val fuelType: String,
    val makeModelKey: String = "",
    val modelId: String = "",
    val fuelId: String = "",
    val cubicCapacity: String = "",
    val seatingCapacity: String = "",
    val exShowroomPrice: String = ""
) {
    /** `1.0 LXI ABS (998CC)`; the capacity is omitted when unknown. */
    val displayLabel: String
        get() = if (cubicCapacity.isNotBlank()) "$variantName (${cubicCapacity}CC)" else variantName
}

/**
 * Fuel types and variants of one Make/Model (beta_GetFuelVariant).
 */
@Immutable
data class FuelVariantCatalog(
    val modelId: String,
    val fuels: List<String>,
    val variants: List<CarVariantItem>
) {
    /**
     * Step 2 options: the API FuelList, plus EXTERNAL FITTED CNG / EXTERNAL FITTED LPG
     * (added by the app) whenever the list contains PETROL.
     */
    val selectableFuels: List<String>
        get() {
            if (fuels.none { it.equals(PETROL, ignoreCase = true) }) return fuels
            val extras = EXTERNAL_FITTED_FUELS.filter { extra -> fuels.none { it.equals(extra, ignoreCase = true) } }
            return fuels + extras
        }

    /**
     * Variants for the selected [fuel] (case-insensitive):
     * - an exact Fuel_Name match, plus
     * - dual-fuel variants whose Fuel_Name contains [fuel] as one part, e.g. a
     *   "PETROL/CNG" variant is listed under PETROL, CNG and PETROL/CNG.
     * Selecting "PETROL/CNG" itself shows only "PETROL/CNG" variants.
     * - EXTERNAL FITTED CNG / LPG (a kit on a petrol car) shows the PETROL variants.
     */
    fun variantsFor(fuel: String?): List<CarVariantItem> {
        if (fuel.isNullOrBlank()) return variants
        val selected = if (isExternalFitted(fuel)) PETROL else fuel.trim()
        return variants.filter { variant ->
            variant.fuelType.equals(selected, ignoreCase = true) ||
                variant.fuelType.fuelParts().any { it.equals(selected, ignoreCase = true) }
        }
    }

    private fun String.fuelParts(): List<String> =
        split('/').map { it.trim() }.filter { it.isNotBlank() }.takeIf { it.size > 1 }.orEmpty()

    companion object {
        const val PETROL = "PETROL"
        const val EXTERNAL_FITTED_CNG = "EXTERNAL FITTED CNG"
        const val EXTERNAL_FITTED_LPG = "EXTERNAL FITTED LPG"
        val EXTERNAL_FITTED_FUELS = listOf(EXTERNAL_FITTED_CNG, EXTERNAL_FITTED_LPG)

        fun isExternalFitted(fuel: String?): Boolean =
            EXTERNAL_FITTED_FUELS.any { it.equals(fuel?.trim(), ignoreCase = true) }
    }
}
