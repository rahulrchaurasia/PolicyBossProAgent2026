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
    val variantId: String,
    val variantName: String,
    val fuelType: String,
    val makeModelKey: String = ""
)
