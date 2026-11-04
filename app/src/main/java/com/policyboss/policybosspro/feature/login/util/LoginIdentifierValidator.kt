package com.policyboss.policybosspro.feature.login.util

enum class LoginIdentifierType {
    MOBILE,
    EMAIL,
    POSP_NUMBER,
    ERP_CODE,
    UNKNOWN
}

sealed class ValidationResult {
    data class Success(val type: LoginIdentifierType, val sanitizedId: String) : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

object LoginIdentifierValidator {

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val ALPHANUMERIC_REGEX = Regex("^[a-zA-Z0-9_-]+$")

    fun validate(input: String): ValidationResult {
        val trimmed = input.trim()

        if (trimmed.isEmpty()) {
            return ValidationResult.Error("Please enter mobile number, email ID, POSP number or ERP code.")
        }

        // 1. Email check
        if (trimmed.contains("@")) {
            return if (EMAIL_REGEX.matches(trimmed)) {
                ValidationResult.Success(LoginIdentifierType.EMAIL, trimmed)
            } else {
                ValidationResult.Error("Please enter a valid email address.")
            }
        }

        // 2. Pure digits check (Mobile or numeric POSP / ERP)
        if (trimmed.all { it.isDigit() }) {
            return when {
                trimmed.length == 10 -> {
                    ValidationResult.Success(LoginIdentifierType.MOBILE, trimmed)
                }
                trimmed.length in 3..9 -> {
                    ValidationResult.Success(LoginIdentifierType.POSP_NUMBER, trimmed)
                }
                else -> {
                    ValidationResult.Error("Please enter a valid 10-digit mobile number, POSP number or ERP code.")
                }
            }
        }

        // 3. Alphanumeric check (POSP / ERP Code)
        if (ALPHANUMERIC_REGEX.matches(trimmed) && trimmed.length in 3..25) {
            return ValidationResult.Success(LoginIdentifierType.ERP_CODE, trimmed)
        }

        return ValidationResult.Error("Please enter a valid mobile number, email ID, POSP number or ERP code.")
    }
}

