package com.policyboss.policybosspro.feature.carInsurance.model

/**
 * Tracks the active modal bottom sheet on the Car Insurance form.
 * Ensures only one sheet is open at a time with clean, type-safe orchestration.
 */
enum class CarInsuranceBottomSheet {
    NONE,
    RTO,
    INSURER,
    TP_INSURER,
    MODEL,
    VARIANT,
    FUEL,
    NCB
}

