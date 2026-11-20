package com.policyboss.policybosspro.feature.home.util

import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VehicleRegistrationValidatorTest {

    // =========================================================================
    // 1. DYNAMIC KEYBOARD RESOLUTION & IME BEHAVIOR
    // =========================================================================

    @Test
    fun `keyboard resolution transitions between Ascii and Number based on segment`() {
        // Empty state: default to Ascii for state code letters
        assertEquals(KeyboardType.Ascii, VehicleRegistrationValidator.resolveKeyboardType("", allowBhSeries = true))
        assertEquals(KeyboardType.Ascii, VehicleRegistrationValidator.resolveKeyboardType("", allowBhSeries = false))

        // State letters: Ascii
        assertEquals(KeyboardType.Ascii, VehicleRegistrationValidator.resolveKeyboardType("R", allowBhSeries = false))
        assertEquals(KeyboardType.Number, VehicleRegistrationValidator.resolveKeyboardType("RJ", allowBhSeries = false))

        // RTO District: Number
        assertEquals(KeyboardType.Number, VehicleRegistrationValidator.resolveKeyboardType("RJ1", allowBhSeries = false))
        assertEquals(KeyboardType.Ascii, VehicleRegistrationValidator.resolveKeyboardType("RJ14", allowBhSeries = false))

        // Series: Ascii
        assertEquals(KeyboardType.Ascii, VehicleRegistrationValidator.resolveKeyboardType("RJ14C", allowBhSeries = false))

        // Common 2-letter series completed: transitions to Number for 4-digit block
        assertEquals(KeyboardType.Number, VehicleRegistrationValidator.resolveKeyboardType("RJ14CV", allowBhSeries = false))

        // 1-letter series closed via Next/hyphen action: switches to Number
        assertEquals(KeyboardType.Ascii, VehicleRegistrationValidator.resolveKeyboardType("DL01S", allowBhSeries = false, isSeriesClosed = false))
        assertEquals(KeyboardType.Number, VehicleRegistrationValidator.resolveKeyboardType("DL01S", allowBhSeries = false, isSeriesClosed = true))

        // Number block digits: Number
        assertEquals(KeyboardType.Number, VehicleRegistrationValidator.resolveKeyboardType("DL01S5", allowBhSeries = false))
        assertEquals(KeyboardType.Number, VehicleRegistrationValidator.resolveKeyboardType("RJ14CV0002", allowBhSeries = false))

        // BH Series transitions:
        assertEquals(KeyboardType.Number, VehicleRegistrationValidator.resolveKeyboardType("2", allowBhSeries = true))
        assertEquals(KeyboardType.Ascii, VehicleRegistrationValidator.resolveKeyboardType("22", allowBhSeries = true))
        assertEquals(KeyboardType.Ascii, VehicleRegistrationValidator.resolveKeyboardType("22B", allowBhSeries = true))
        assertEquals(KeyboardType.Number, VehicleRegistrationValidator.resolveKeyboardType("22BH", allowBhSeries = true))
        assertEquals(KeyboardType.Number, VehicleRegistrationValidator.resolveKeyboardType("22BH123", allowBhSeries = true))
        assertEquals(KeyboardType.Ascii, VehicleRegistrationValidator.resolveKeyboardType("22BH1234", allowBhSeries = true))
        assertEquals(KeyboardType.Ascii, VehicleRegistrationValidator.resolveKeyboardType("22BH1234A", allowBhSeries = true))
    }

    @Test
    fun `capitalization correctly resolves based on KeyboardType`() {
        assertEquals(KeyboardCapitalization.Characters, VehicleRegistrationValidator.resolveCapitalization(KeyboardType.Ascii))
        assertEquals(KeyboardCapitalization.None, VehicleRegistrationValidator.resolveCapitalization(KeyboardType.Number))
    }

    @Test
    fun `isNumberBlockStarted and getSeriesLength correctly track segments`() {
        assertFalse(VehicleRegistrationValidator.isNumberBlockStarted("RJ14CV", allowBhSeries = true))
        assertTrue(VehicleRegistrationValidator.isNumberBlockStarted("RJ14CV0", allowBhSeries = true))
        assertTrue(VehicleRegistrationValidator.isNumberBlockStarted("DL01S5", allowBhSeries = true))
        assertTrue(VehicleRegistrationValidator.isNumberBlockStarted("22BH1", allowBhSeries = true))

        assertEquals(0, VehicleRegistrationValidator.getSeriesLength("RJ14", allowBhSeries = true))
        assertEquals(1, VehicleRegistrationValidator.getSeriesLength("DL01S", allowBhSeries = true))
        assertEquals(2, VehicleRegistrationValidator.getSeriesLength("RJ14CV", allowBhSeries = true))
        assertEquals(3, VehicleRegistrationValidator.getSeriesLength("MH01ABC", allowBhSeries = true))
    }

    // =========================================================================
    // 2. CATEGORY-SPECIFIC BH SERIES & PLACEHOLDER RULES
    // =========================================================================

    @Test
    fun `allowsBhSeries is true for Car and TW, false for CV`() {
        assertTrue(VehicleRegistrationValidator.allowsBhSeries("Car"))
        assertTrue(VehicleRegistrationValidator.allowsBhSeries("CAR"))
        assertTrue(VehicleRegistrationValidator.allowsBhSeries("TW"))
        assertTrue(VehicleRegistrationValidator.allowsBhSeries("Two Wheeler"))
        assertFalse(VehicleRegistrationValidator.allowsBhSeries("CV"))
        assertFalse(VehicleRegistrationValidator.allowsBhSeries("Commercial Vehicle"))
    }

    @Test
    fun `exact placeholders without register keyword`() {
        assertEquals("Enter car number", VehicleRegistrationValidator.getPlaceholder("Car"))
        assertEquals("Enter two wheeler number", VehicleRegistrationValidator.getPlaceholder("TW"))
        assertEquals("Enter commercial vehicle number", VehicleRegistrationValidator.getPlaceholder("CV"))
        assertEquals("Enter commercial vehicle number", VehicleRegistrationValidator.getPlaceholder("Commercial Vehicle"))
    }

    // =========================================================================
    // 3. VALIDATION AND ERROR MESSAGES
    // =========================================================================

    @Test
    fun `validation accepts standard formats and rejects invalid inputs`() {
        // Car Real-World Indian Number Plates across states & series formats
        val validCarPlates = listOf(
            "RJ14CV0002",  // Rajasthan, 2-letter series with leading zero
            "MH02AB1234",  // Maharashtra, 2-letter series
            "DL01S5678",   // Delhi, 1-letter series
            "KA01MJ9999",  // Karnataka, 2-letter series
            "TN09BK4567",  // Tamil Nadu
            "UP32AB1111",  // Uttar Pradesh
            "GJ01AB1234",  // Gujarat
            "TS09FA1234",  // Telangana
            "WB02AB1234",  // West Bengal
            "KL07AB1234",  // Kerala
            "HR26DQ1234",  // Haryana
            "CH01AB1234",  // Chandigarh
            "MH01ABC1234", // 3-letter series
            "21BH1234AA",  // Bharat Series 2021
            "22BH1234AA",  // Bharat Series 2022
            "23BH9999Z",   // Bharat Series 2023 1-letter
            "24BH0001AB"   // Bharat Series 2024 leading zeros
        )
        for (plate in validCarPlates) {
            assertTrue("Car plate $plate should be valid", VehicleRegistrationValidator.isValid("Car", plate))
            assertNull("Car plate $plate should have no validation error", VehicleRegistrationValidator.getValidationError("Car", plate))
            // Pasted / hyphenated form should also validate
            val formatted = VehicleRegistrationFormatter.format(plate, allowBhSeries = true).formatted
            assertTrue("Formatted car plate $formatted should be valid", VehicleRegistrationValidator.isValid("Car", formatted))
        }

        assertFalse(VehicleRegistrationValidator.isValid("Car", "1234"))
        assertFalse(VehicleRegistrationValidator.isValid("Car", "MH"))
        assertFalse(VehicleRegistrationValidator.isValid("Car", "MH02"))

        // Two Wheeler Real-World Indian Number Plates
        val validTwPlates = listOf(
            "MH02AB1234",  // Mumbai Two Wheeler
            "DL01S5678",   // Delhi Two Wheeler (S series)
            "KA05EV9999",  // Bangalore South Two Wheeler
            "RJ14CV0002",  // Jaipur Two Wheeler
            "TN07CE1234",  // Chennai Two Wheeler
            "UP16AB1234",  // Noida Two Wheeler
            "GJ05AB1234",  // Surat Two Wheeler
            "22BH1234AA",  // Bharat Series Two Wheeler
            "23BH5678BB"   // Bharat Series Two Wheeler
        )
        for (plate in validTwPlates) {
            assertTrue("Two Wheeler plate $plate should be valid", VehicleRegistrationValidator.isValid("TW", plate))
            assertNull("Two Wheeler plate $plate should have no validation error", VehicleRegistrationValidator.getValidationError("TW", plate))
            val formatted = VehicleRegistrationFormatter.format(plate, allowBhSeries = true).formatted
            assertTrue("Formatted TW plate $formatted should be valid", VehicleRegistrationValidator.isValid("TW", formatted))
        }

        assertFalse(VehicleRegistrationValidator.isValid("TW", "1234"))
        assertFalse(VehicleRegistrationValidator.isValid("TW", "MH"))
        assertFalse(VehicleRegistrationValidator.isValid("TW", "MH02"))

        // CV
        assertTrue(VehicleRegistrationValidator.isValid("CV", "RJ14CV0002"))
        assertTrue(VehicleRegistrationValidator.isValid("CV", "MH12Q1234"))
        assertFalse(VehicleRegistrationValidator.isValid("CV", "12"))
    }

    @Test
    fun `validation error messages match specifications`() {
        // Blank
        assertEquals("Please enter car number", VehicleRegistrationValidator.getValidationError("Car", ""))
        assertEquals("Please enter two wheeler number", VehicleRegistrationValidator.getValidationError("TW", ""))
        assertEquals("Please enter commercial vehicle number", VehicleRegistrationValidator.getValidationError("CV", ""))

        // Invalid
        assertEquals("Please enter a valid car number (e.g. RJ14CV0002 or 22BH1234AA)", VehicleRegistrationValidator.getValidationError("Car", "123"))
        assertEquals("Please enter a valid two wheeler number (e.g. MH02AB1234 or DL01S5678)", VehicleRegistrationValidator.getValidationError("TW", "123"))
        assertEquals("Please enter a valid commercial vehicle number", VehicleRegistrationValidator.getValidationError("CV", "12"))

        // Valid returns null
        assertNull(VehicleRegistrationValidator.getValidationError("Car", "RJ14CV0002"))
        assertNull(VehicleRegistrationValidator.getValidationError("TW", "MH02AB1234"))
        assertNull(VehicleRegistrationValidator.getValidationError("CV", "RJ14CV0002"))
    }
}

