package com.policyboss.policybosspro.core.utils

import com.policyboss.policybosspro.feature.login.data.model.LoginNewResponse_DSAS_Horizon

/**
 * Centralized domain parser for Horizon login responses ([LoginNewResponse_DSAS_Horizon])
 * and profile initials generation.
 *
 * Encapsulates business rules across user types (`POSP`, `FOS`, `EMP`, `MISP`)
 * in a pure, unit-testable utility decoupled from UI and storage layers.
 */
object UserProfileParser {

    const val DEFAULT_INITIALS = "PB"

    private fun String?.cleanOrNull(): String? =
        this?.trim()?.takeIf {
            it.isNotBlank() &&
                !it.equals("NA", ignoreCase = true) &&
                !it.equals("null", ignoreCase = true)
        }

    private fun normalizedUserType(response: LoginNewResponse_DSAS_Horizon?): String =
        response?.user_type.orEmpty().trim().uppercase()

    /**
     * Resolves the display name across user types (`POSP`, `FOS`, `EMP`, `MISP`)
     * adhering to Horizon business rules:
     * - `POSP` / `FOS`: `POSP_USER.Name_On_PAN`, falling back to `POSP` first+last name, then `EMP.Emp_Name`.
     * - `EMP` / `MISP`: `EMP.Emp_Name`.
     */
    fun extractName(response: LoginNewResponse_DSAS_Horizon?): String {
        if (response == null) return ""

        return when (normalizedUserType(response)) {
            "POSP", "FOS" -> {
                response.POSP_USER?.Name_On_PAN.cleanOrNull()
                    ?: listOfNotNull(
                        response.POSP?.First_Name.cleanOrNull(),
                        response.POSP?.Last_Name.cleanOrNull()
                    ).joinToString(" ").cleanOrNull()
                    ?: response.EMP?.Emp_Name.cleanOrNull()
                    ?: ""
            }
            "EMP", "MISP" -> response.EMP?.Emp_Name.cleanOrNull() ?: ""
            else -> ""
        }
    }

    /**
     * Extracts initials strictly using the first and last word when 2 or more words exist.
     *
     * Examples:
     * - `"Rahul Rajendra Chaurasia"` -> `"RC"` (First and Last)
     * - `"Amit Kumar"` -> `"AK"`
     * - `"Suresh"` -> `"SU"` (First 2 chars)
     * - `"A"` -> `"A"`
     * - `""` -> `"PB"` (Fallback)
     */
    fun extractInitials(
        fullName: String,
        defaultFallback: String = DEFAULT_INITIALS
    ): String {
        val trimmed = fullName.trim()
        if (trimmed.isEmpty()) return defaultFallback

        val tokens = trimmed
            .split("\\s+".toRegex())
            .mapNotNull { token ->
                val cleaned = token.trim()
                cleaned.takeIf { it.any(Char::isLetterOrDigit) }
            }

        return when {
            tokens.size >= 2 -> {
                val firstInitial = tokens.first().first(Char::isLetterOrDigit).uppercaseChar()
                val lastInitial = tokens.last().first(Char::isLetterOrDigit).uppercaseChar()
                "$firstInitial$lastInitial"
            }
            tokens.size == 1 -> {
                val alnum = tokens.first().filter(Char::isLetterOrDigit)
                when {
                    alnum.length >= 2 -> alnum.take(2).uppercase()
                    alnum.length == 1 -> alnum.uppercase()
                    else -> defaultFallback
                }
            }
            else -> defaultFallback
        }
    }

    /**
     * Resolves the registered mobile number across user types (`POSP`, `FOS`, `EMP`, `MISP`).
     */
    fun extractMobile(response: LoginNewResponse_DSAS_Horizon?): String {
        if (response == null) return ""

        return when (normalizedUserType(response)) {
            "POSP", "FOS" -> {
                response.POSP_USER?.Mobile_No.cleanOrNull()
                    ?: response.POSP?.Mobile_No.cleanOrNull()
                    ?: response.EMP?.Mobile_Number.cleanOrNull()
                    ?: ""
            }
            "EMP", "MISP" -> response.EMP?.Mobile_Number.cleanOrNull() ?: ""
            else -> ""
        }
    }

    /**
     * Resolves the registered email address across user types (`POSP`, `FOS`, `EMP`, `MISP`).
     */
    fun extractEmail(response: LoginNewResponse_DSAS_Horizon?): String {
        if (response == null) return ""

        return when (normalizedUserType(response)) {
            "POSP", "FOS" -> {
                response.POSP_USER?.Email_Id.cleanOrNull()
                    ?: response.POSP?.Email_Id.cleanOrNull()
                    ?: response.EMP?.Email_Id.cleanOrNull()
                    ?: ""
            }
            "EMP", "MISP" -> response.EMP?.Email_Id.cleanOrNull() ?: ""
            else -> ""
        }
    }

    /**
     * Resolves FBA ID across `POSP`/`FOS` (`POSP.Fba_Id`) and `EMP`/`MISP` (`EMP.FBA_ID`).
     * Defaults to `"0"` when unavailable.
     */
    fun extractFbaId(response: LoginNewResponse_DSAS_Horizon?): String {
        if (response == null) return "0"

        return when (normalizedUserType(response)) {
            "POSP", "FOS" -> response.POSP?.Fba_Id.cleanOrNull()?.takeIf { it != "0" } ?: "0"
            "EMP", "MISP" -> response.EMP?.FBA_ID.cleanOrNull()?.takeIf { it != "0" } ?: "0"
            else -> "0"
        }
    }

    /**
     * Resolves SS ID from root `Ss_Id`, falling back to `POSP.Ss_Id` or `EMP.Emp_Id`.
     * Defaults to `"0"` when unavailable.
     */
    fun extractSsId(response: LoginNewResponse_DSAS_Horizon?): String {
        if (response == null) return "0"

        val rootSsId = response.Ss_Id.cleanOrNull()?.takeIf { it != "0" }
        if (rootSsId != null) return rootSsId

        return when (normalizedUserType(response)) {
            "POSP", "FOS" -> response.POSP?.Ss_Id.cleanOrNull()
                ?: response.EMP?.Emp_Id.cleanOrNull()
            "EMP", "MISP" -> response.EMP?.Emp_Id.cleanOrNull()
            else -> response.POSP?.Ss_Id.cleanOrNull()
                ?: response.EMP?.Emp_Id.cleanOrNull()
        }?.takeIf { it != "0" } ?: "0"
    }

    /**
     * Resolves ERP ID from `POSP_USER.Erp_Id`, falling back to `POSP.Erp_Id`.
     * Defaults to `"0"` when unavailable.
     */
    fun extractErpId(response: LoginNewResponse_DSAS_Horizon?): String {
        if (response == null) return "0"

        return response.POSP_USER?.Erp_Id.cleanOrNull()?.takeIf { it != "0" }
            ?: response.POSP?.Erp_Id.cleanOrNull()?.takeIf { it != "0" }
            ?: "0"
    }

    /**
     * Resolves designation from `HR.Designation`, returning `""` when missing or `"NA"`.
     */
    fun extractDesignation(response: LoginNewResponse_DSAS_Horizon?): String {
        return response?.HR?.Designation.cleanOrNull().orEmpty()
    }

    /**
     * Formats permanent address components from `POSP_USER` into a comma-separated string.
     */
    fun extractAddress(response: LoginNewResponse_DSAS_Horizon?): String {
        val pospUser = response?.POSP_USER ?: return ""

        return listOfNotNull(
            pospUser.Permanant_Add1.cleanOrNull(),
            pospUser.Permanant_Add2.cleanOrNull(),
            pospUser.Permanant_Add3.cleanOrNull(),
            pospUser.Permanant_City.cleanOrNull(),
            pospUser.Permanant_State.cleanOrNull(),
            pospUser.Permanant_Pincode.cleanOrNull()
        ).joinToString(", ")
    }
}

