package com.policyboss.policybosspro.feature.home.util

import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType

/**
 * Validates Indian vehicle registration numbers and provides category-specific
 * placeholders, keyboard resolution, and error feedback messages.
 *
 * Supports:
 * - Standard Indian RTO format: `SS-RR-SERIES-NNNN` (e.g. `RJ-14-CV-0002`, `MH-02-AB-1234`, `DL-01-S-5678`, `MH-01-ABC-1234`)
 * - Bharat (BH) Series format: `YY-BH-NNNN-XX` (e.g. `22-BH-1234-AA`)
 */
object VehicleRegistrationValidator {

    // Standard Indian RTO Regex: ^[A-Z]{2}[0-9]{1,2}[A-Z]{1,3}[0-9]{4}$
    // Accepts RJ14CV0002, MH02AB1234, DL01S5678, MH01ABC1234
    val STANDARD_RTO_REGEX = "^[A-Z]{2}[0-9]{1,2}[A-Z]{1,3}[0-9]{4}$".toRegex()

    // Bharat (BH) Series Regex: ^[0-9]{2}BH[0-9]{4}[A-Z]{1,2}$
    // Accepts 22BH1234AA, 22BH1234A
    val BH_SERIES_REGEX = "^[0-9]{2}BH[0-9]{4}[A-Z]{1,2}$".toRegex()

    // Two Wheeler Regex: SS-RR-LL-NNNN (e.g. MH02AB1234, DL01S5678)
    val TW_REGEX = "^[A-Z]{2}[0-9]{1,2}[A-Z]{1,2}[0-9]{4}$".toRegex()

    /**
     * Determines whether Bharat (BH) series format is permitted for the given vehicle category.
     * True for Car and Two Wheeler; False for Commercial Vehicle.
     */
    fun allowsBhSeries(category: String): Boolean {
        return when (category.uppercase()) {
            "CAR" -> true
            "TW", "TWO WHEELER", "TWO-WHEELER", "BIKE" -> true
            "CV", "COMMERCIAL VEHICLE" -> false
            else -> false
        }
    }

    /**
     * Normalizes a registration number string by stripping hyphens and spaces
     * and converting all characters to uppercase.
     */
    fun cleanRegistrationNumber(input: String): String {
        return VehicleRegistrationFormatter.clean(input)
    }

    /**
     * Filters user input character-by-character according to vehicle registration rules:
     * - Disallows invalid characters at specific positions (e.g. digits in state code, letters in RTO code)
     * - Supports BH series when [allowBhSeries] is true
     * - Returns uppercase alphanumeric string without hyphens or spaces
     */
    fun filterVehicleRegistration(input: String, allowBhSeries: Boolean): String {
        val normalized = cleanRegistrationNumber(input)
        if (normalized.isEmpty()) return ""

        val isBh = allowBhSeries && normalized.first().isDigit()

        return if (isBh) {
            buildString {
                normalized.forEach { char ->
                    when (length) {
                        0, 1 -> if (char.isDigit()) append(char)
                        2 -> if (char == 'B' || char.isLetter()) append('B')
                        3 -> if (char == 'H' || char.isLetter()) append('H')
                        4, 5, 6, 7 -> if (char.isDigit()) append(char)
                        8, 9 -> if (char.isLetter()) append(char)
                    }
                    if (length == 10) return@buildString
                }
            }
        } else {
            buildString {
                var inNumberBlock = false
                var numberDigitsCount = 0

                normalized.forEach { char ->
                    when (length) {
                        0, 1 -> {
                            // State Code (2 letters, e.g. MH, RJ, DL)
                            if (char.isLetter()) append(char)
                        }
                        2, 3 -> {
                            // RTO District Code (2 digits, e.g. 02, 14, 01)
                            if (char.isDigit()) append(char)
                        }
                        4 -> {
                            // 1st Series Letter (e.g. 'A' in MH02AB, 'S' in DL01S)
                            if (char.isLetter()) append(char)
                        }
                        5 -> {
                            // Could be 2nd series letter (MH02AB) or 1st number digit (DL01S5)
                            if (char.isLetter()) {
                                append(char)
                            } else if (char.isDigit()) {
                                inNumberBlock = true
                                numberDigitsCount = 1
                                append(char)
                            }
                        }
                        6 -> {
                            if (inNumberBlock) {
                                if (char.isDigit() && numberDigitsCount < 4) {
                                    append(char)
                                    numberDigitsCount++
                                }
                            } else {
                                // Currently has 2 series letters (MH02AB). Could be 3rd letter or 1st number digit
                                if (char.isLetter()) {
                                    append(char)
                                } else if (char.isDigit()) {
                                    inNumberBlock = true
                                    numberDigitsCount = 1
                                    append(char)
                                }
                            }
                        }
                        7 -> {
                            if (inNumberBlock) {
                                if (char.isDigit() && numberDigitsCount < 4) {
                                    append(char)
                                    numberDigitsCount++
                                }
                            } else {
                                // Had 3 series letters, number block MUST start
                                if (char.isDigit()) {
                                    inNumberBlock = true
                                    numberDigitsCount = 1
                                    append(char)
                                }
                            }
                        }
                        else -> {
                            // Remaining digits up to 4 total
                            if (char.isDigit() && numberDigitsCount < 4) {
                                append(char)
                                numberDigitsCount++
                            }
                        }
                    }
                    if (inNumberBlock && numberDigitsCount == 4) {
                        return@buildString
                    }
                }
            }
        }
    }

    /**
     * Sanitizes input while typing.
     */
    fun sanitizeInput(input: String, allowBhSeries: Boolean = true): String {
        return filterVehicleRegistration(input, allowBhSeries)
    }

    /**
     * Returns the number of series letters currently present in the normalized input string.
     */
    fun getSeriesLength(clean: String, allowBhSeries: Boolean): Int {
        val segments = VehicleRegistrationFormatter.parseSegments(clean, allowBhSeries)
        return if (segments.isBh) {
            segments.seg4.length
        } else {
            segments.seg3.length
        }
    }

    /**
     * Returns true if the user has started entering the 4-digit number block.
     */
    fun isNumberBlockStarted(clean: String, allowBhSeries: Boolean): Boolean {
        val segments = VehicleRegistrationFormatter.parseSegments(clean, allowBhSeries)
        return if (segments.isBh) {
            segments.seg3.isNotEmpty()
        } else {
            segments.seg4.isNotEmpty()
        }
    }

    /**
     * Resolves dynamic [KeyboardType] based on current string length, format stage,
     * and explicit series completion.
     */
    fun resolveKeyboardType(
        clean: String,
        allowBhSeries: Boolean,
        isSeriesClosed: Boolean = false
    ): KeyboardType {
        if (clean.isEmpty()) {
            return KeyboardType.Ascii
        }

        val isBh = allowBhSeries && clean.first().isDigit()
        return if (isBh) {
            when (clean.length) {
                0, 1 -> KeyboardType.Number
                2, 3 -> KeyboardType.Ascii
                in 4..7 -> KeyboardType.Number
                else -> KeyboardType.Ascii
            }
        } else {
            val segments = VehicleRegistrationFormatter.parseSegments(clean, allowBhSeries = false)
            when {
                // State code (0..1 letters)
                segments.seg1.length < 2 -> KeyboardType.Ascii
                // RTO code (0..1 digits after 2-letter state)
                segments.seg2.length < 2 -> KeyboardType.Number
                // Number block already started
                segments.seg4.isNotEmpty() -> KeyboardType.Number
                // Explicitly closed series (e.g. DL-01-S-)
                isSeriesClosed -> KeyboardType.Number
                // Series letters
                segments.seg3.length == 0 -> KeyboardType.Ascii
                segments.seg3.length == 1 -> KeyboardType.Ascii
                segments.seg3.length == 2 -> KeyboardType.Number // Common 2-letter series completed, ready for digits
                segments.seg3.length >= 3 -> KeyboardType.Number // 3-letter series completed, ready for digits
                else -> KeyboardType.Number
            }
        }
    }

    /**
     * Resolves dynamic [KeyboardCapitalization] based on resolved [KeyboardType].
     */
    fun resolveCapitalization(keyboardType: KeyboardType): KeyboardCapitalization {
        return if (keyboardType == KeyboardType.Ascii) {
            KeyboardCapitalization.Characters
        } else {
            KeyboardCapitalization.None
        }
    }

    /**
     * Checks if a registration number is valid for the given vehicle category.
     */
    fun isValid(category: String, regNo: String): Boolean {
        val clean = cleanRegistrationNumber(regNo)
        if (clean.isBlank()) return false

        return when (category.uppercase()) {
            "CAR" -> {
                STANDARD_RTO_REGEX.matches(clean) || BH_SERIES_REGEX.matches(clean)
            }
            "TW", "TWO WHEELER", "TWO-WHEELER", "BIKE" -> {
                STANDARD_RTO_REGEX.matches(clean) || BH_SERIES_REGEX.matches(clean)
            }
            "CV", "COMMERCIAL VEHICLE" -> {
                // Preserves existing CV behavior while validating standard patterns
                STANDARD_RTO_REGEX.matches(clean) || clean.length in 4..15
            }
            else -> clean.length in 4..15
        }
    }

    /**
     * Returns dynamic placeholder text based on the selected vehicle category.
     * Strictly omits "register".
     */
    fun getPlaceholder(category: String): String {
        return when (category.uppercase()) {
            "CAR" -> "Enter car number"
            "TW", "TWO WHEELER", "TWO-WHEELER", "BIKE" -> "Enter two wheeler number"
            "CV", "COMMERCIAL VEHICLE" -> "Enter commercial vehicle number"
            else -> "Enter vehicle number"
        }
    }

    /**
     * Returns a user-facing validation error message if invalid, or null if valid.
     */
    fun getValidationError(category: String, regNo: String): String? {
        if (regNo.isBlank()) {
            return when (category.uppercase()) {
                "CAR" -> "Please enter car number"
                "TW", "TWO WHEELER", "TWO-WHEELER", "BIKE" -> "Please enter two wheeler number"
                "CV", "COMMERCIAL VEHICLE" -> "Please enter commercial vehicle number"
                else -> "Please enter vehicle number"
            }
        }

        val clean = cleanRegistrationNumber(regNo)
        return when (category.uppercase()) {
            "CAR" -> {
                if (!STANDARD_RTO_REGEX.matches(clean) && !BH_SERIES_REGEX.matches(clean)) {
                    "Please enter a valid car number (e.g. RJ14CV0002 or 22BH1234AA)"
                } else null
            }
            "TW", "TWO WHEELER", "TWO-WHEELER", "BIKE" -> {
                if (!STANDARD_RTO_REGEX.matches(clean) && !BH_SERIES_REGEX.matches(clean)) {
                    "Please enter a valid two wheeler number (e.g. MH02AB1234 or DL01S5678)"
                } else null
            }
            "CV", "COMMERCIAL VEHICLE" -> {
                if (clean.length < 4) {
                    "Please enter a valid commercial vehicle number"
                } else null
            }
            else -> null
        }
    }
}
