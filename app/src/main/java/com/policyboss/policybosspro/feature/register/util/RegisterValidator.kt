package com.policyboss.policybosspro.feature.register.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object RegisterValidator {

    private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    private val PAN_REGEX = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$".toRegex()
    private val PINCODE_REGEX = "^[1-9][0-9]{5}$".toRegex()

    fun validateFullName(name: String): String? {
        val trimmed = name.trim()
        return when {
            trimmed.isEmpty() -> "Please enter your full name"
            trimmed.length < 2 -> "Name must be at least 2 characters"
            !trimmed.all { it.isLetter() || it.isWhitespace() } -> "Name must contain only alphabets and spaces"
            else -> null
        }
    }

    fun validateMobile(mobile: String): String? {
        val trimmed = mobile.trim()
        return when {
            trimmed.isEmpty() || trimmed.length != 10 -> "Please enter a valid 10-digit mobile number"
            !trimmed.all { it.isDigit() } -> "Mobile number must contain digits only"
            trimmed.first() !in '6'..'9' -> "Mobile number must start with 6, 7, 8, or 9"
            else -> null
        }
    }

    fun validateEmail(email: String): String? {
        val trimmed = email.trim()
        return when {
            trimmed.isEmpty() || !EMAIL_REGEX.matches(trimmed) -> "Please enter a valid email address"
            else -> null
        }
    }

    fun validateDob(dob: String): String? {
        val trimmed = dob.trim()
        if (trimmed.isEmpty()) return "Please select your date of birth"

        return try {
            // Support both dd/MM/yyyy and yyyy-MM-dd
            val format = if (trimmed.contains("/")) {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            } else {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            }
            format.isLenient = false
            val parsedDate = format.parse(trimmed)
            if (parsedDate != null && parsedDate.after(Date())) {
                "Date of Birth cannot be in the future"
            } else {
                null
            }
        } catch (e: Exception) {
            "Please select a valid date of birth"
        }
    }

    fun validatePincode(pincode: String): String? {
        val trimmed = pincode.trim()
        return when {
            trimmed.isEmpty() || trimmed.length != 6 || !PINCODE_REGEX.matches(trimmed) -> "Please enter a valid 6-digit pincode"
            else -> null
        }
    }

    fun validateCity(city: String): String? {
        val trimmed = city.trim()
        return when {
            trimmed.isEmpty() -> "Please enter your city"
            else -> null
        }
    }

    fun validatePan(pan: String): String? {
        val normalized = pan.trim().uppercase()
        return when {
            normalized.isEmpty() || normalized.length != 10 || !PAN_REGEX.matches(normalized) -> "Please enter a valid PAN number"
            else -> null
        }
    }

    fun validateGender(gender: String): String? {
        return if (gender.isBlank() || (gender != "M" && gender != "F")) {
            "Please select your gender"
        } else null
    }

    fun validateSubscription(subscription: String): String? {
        return if (subscription.isBlank()) {
            "Please select a subscription type"
        } else null
    }

    fun validateExistingAgent(isExisting: Boolean?): String? {
        return if (isExisting == null) {
            "Please select Yes or No"
        } else null
    }

    /**
     * Sequential validation in the exact required order:
     * 1. Full Name
     * 2. Mobile
     * 3. Email
     * 4. DOB
     * 5. Pincode
     * 6. City (Mandatory)
     * 7. PAN
     * 8. Gender
     * 9. Subscription Type
     * 10. Existing Agent / POSP (Yes/No)
     *
     * Returns a Pair of (fieldName, errorMessage) for the first failing field,
     * or null if all fields are valid.
     */
    fun validateSequential(
        fullName: String,
        mobile: String,
        email: String,
        dob: String,
        pincode: String,
        city: String,
        pan: String,
        gender: String,
        subscriptionType: String,
        isExistingAgent: Boolean?
    ): Pair<String, String>? {
        return when {
            validateFullName(fullName) != null -> "fullName" to validateFullName(fullName)!!
            validateMobile(mobile) != null -> "mobile" to validateMobile(mobile)!!
            validateEmail(email) != null -> "email" to validateEmail(email)!!
            validateDob(dob) != null -> "dob" to validateDob(dob)!!
            validatePincode(pincode) != null -> "pincode" to validatePincode(pincode)!!
            validateCity(city) != null -> "city" to validateCity(city)!!
            validatePan(pan) != null -> "pan" to validatePan(pan)!!
            validateGender(gender) != null -> "gender" to validateGender(gender)!!
            validateSubscription(subscriptionType) != null -> "subscriptionType" to validateSubscription(subscriptionType)!!
            validateExistingAgent(isExistingAgent) != null -> "existingAgent" to validateExistingAgent(isExistingAgent)!!
            else -> null
        }
    }
}

