package com.policyboss.policybosspro.feature.carInsurance.model

/**
 * Strongly typed Policy Type representation for Car Insurance.
 * Defines the core business flow:
 * - [TP_ONLY]: Third Party Only coverage.
 * - [OD_ONLY]: Standalone Own Damage coverage.
 * - [COMPREHENSIVE]: Full Comprehensive package policy.
 */
enum class PolicyType(
    val displayName: String,
    /** `vehicle_insurance_subtype` sent to `quote/premium_initiate`. */
    val apiSubtype: String
) {
    TP_ONLY("TP Only", "0CH_1TP"),
    OD_ONLY("OD Only", "1OD_0TP"),
    COMPREHENSIVE("Comprehensive", "1CH_0TP");

    companion object {
        fun fromString(value: String?): PolicyType {
            if (value.isNullOrBlank()) return COMPREHENSIVE
            return when {
                value.equals("TP (3 Yr)", ignoreCase = true) || value.equals("TP Only", ignoreCase = true) || value.equals("TP", ignoreCase = true) -> TP_ONLY
                value.equals("OD Only", ignoreCase = true) || value.equals("OD", ignoreCase = true) || value.contains("SAOD", ignoreCase = true) -> OD_ONLY
                else -> COMPREHENSIVE
            }
        }
    }
}
