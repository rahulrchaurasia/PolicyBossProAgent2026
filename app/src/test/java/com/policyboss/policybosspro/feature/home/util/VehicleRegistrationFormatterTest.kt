package com.policyboss.policybosspro.feature.home.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VehicleRegistrationFormatterTest {

    // =========================================================================
    // 1. FULL FORMATTING TESTS
    // =========================================================================

    @Test
    fun `format standard Car registration number RJ-14-CV-0002`() {
        val result = VehicleRegistrationFormatter.format("RJ14CV0002", allowBhSeries = true)
        assertEquals("RJ-14-CV-0002", result.formatted)
        assertEquals("RJ14CV0002", result.raw)
        assertEquals("RJ", result.segments.seg1)
        assertEquals("14", result.segments.seg2)
        assertEquals("CV", result.segments.seg3)
        assertEquals("0002", result.segments.seg4)
    }

    @Test
    fun `format standard Two Wheeler registration number MH-02-AB-1234`() {
        val result = VehicleRegistrationFormatter.format("MH02AB1234", allowBhSeries = true)
        assertEquals("MH-02-AB-1234", result.formatted)
        assertEquals("MH", result.segments.seg1)
        assertEquals("02", result.segments.seg2)
        assertEquals("AB", result.segments.seg3)
        assertEquals("1234", result.segments.seg4)
    }

    @Test
    fun `format 1-letter series registration number DL-01-S-5678`() {
        val result = VehicleRegistrationFormatter.format("DL01S5678", allowBhSeries = true)
        assertEquals("DL-01-S-5678", result.formatted)
        assertEquals("DL", result.segments.seg1)
        assertEquals("01", result.segments.seg2)
        assertEquals("S", result.segments.seg3)
        assertEquals("5678", result.segments.seg4)
    }

    @Test
    fun `format 3-letter series registration number MH-01-ABC-1234`() {
        val result = VehicleRegistrationFormatter.format("MH01ABC1234", allowBhSeries = true)
        assertEquals("MH-01-ABC-1234", result.formatted)
        assertEquals("MH", result.segments.seg1)
        assertEquals("01", result.segments.seg2)
        assertEquals("ABC", result.segments.seg3)
        assertEquals("1234", result.segments.seg4)
    }

    @Test
    fun `format Bharat Series registration number 22-BH-1234-AA`() {
        val result = VehicleRegistrationFormatter.format("22BH1234AA", allowBhSeries = true)
        assertEquals("22-BH-1234-AA", result.formatted)
        assertTrue(result.segments.isBh)
        assertEquals("22", result.segments.seg1)
        assertEquals("BH", result.segments.seg2)
        assertEquals("1234", result.segments.seg3)
        assertEquals("AA", result.segments.seg4)
    }

    @Test
    fun `format Bharat Series registration number with 1-letter series 22-BH-1234-A`() {
        val result = VehicleRegistrationFormatter.format("22BH1234A", allowBhSeries = true)
        assertEquals("22-BH-1234-A", result.formatted)
        assertEquals("A", result.segments.seg4)
    }

    // =========================================================================
    // 2. TYPING PROGRESSION & AUTO-HYPHEN TESTS
    // =========================================================================

    @Test
    fun `standard RTO typing progression auto-inserts hyphens at segment boundaries`() {
        assertEquals("", VehicleRegistrationFormatter.format("", allowBhSeries = true).formatted)
        assertEquals("R", VehicleRegistrationFormatter.format("R", allowBhSeries = true).formatted)
        assertEquals("RJ-", VehicleRegistrationFormatter.format("RJ", allowBhSeries = true).formatted)
        assertEquals("RJ-1", VehicleRegistrationFormatter.format("RJ1", allowBhSeries = true).formatted)
        assertEquals("RJ-14-", VehicleRegistrationFormatter.format("RJ14", allowBhSeries = true).formatted)
        assertEquals("RJ-14-C", VehicleRegistrationFormatter.format("RJ14C", allowBhSeries = true).formatted)
        assertEquals("RJ-14-CV-", VehicleRegistrationFormatter.format("RJ14CV", allowBhSeries = true).formatted)
        assertEquals("RJ-14-CV-0", VehicleRegistrationFormatter.format("RJ14CV0", allowBhSeries = true).formatted)
        assertEquals("RJ-14-CV-00", VehicleRegistrationFormatter.format("RJ14CV00", allowBhSeries = true).formatted)
        assertEquals("RJ-14-CV-000", VehicleRegistrationFormatter.format("RJ14CV000", allowBhSeries = true).formatted)
        assertEquals("RJ-14-CV-0002", VehicleRegistrationFormatter.format("RJ14CV0002", allowBhSeries = true).formatted)
    }

    @Test
    fun `explicitly closing 1-letter series appends hyphen to series segment`() {
        // Without explicit close:
        assertEquals("DL-01-S", VehicleRegistrationFormatter.format("DL01S", allowBhSeries = true, isSeriesClosed = false).formatted)
        // With explicit close:
        assertEquals("DL-01-S-", VehicleRegistrationFormatter.format("DL01S", allowBhSeries = true, isSeriesClosed = true).formatted)
    }

    @Test
    fun `BH series typing progression auto-inserts hyphens`() {
        assertEquals("2", VehicleRegistrationFormatter.format("2", allowBhSeries = true).formatted)
        assertEquals("22-", VehicleRegistrationFormatter.format("22", allowBhSeries = true).formatted)
        assertEquals("22-B", VehicleRegistrationFormatter.format("22B", allowBhSeries = true).formatted)
        assertEquals("22-BH-", VehicleRegistrationFormatter.format("22BH", allowBhSeries = true).formatted)
        assertEquals("22-BH-1", VehicleRegistrationFormatter.format("22BH1", allowBhSeries = true).formatted)
        assertEquals("22-BH-1234-", VehicleRegistrationFormatter.format("22BH1234", allowBhSeries = true).formatted)
        assertEquals("22-BH-1234-A", VehicleRegistrationFormatter.format("22BH1234A", allowBhSeries = true).formatted)
        assertEquals("22-BH-1234-AA", VehicleRegistrationFormatter.format("22BH1234AA", allowBhSeries = true).formatted)
    }

    // =========================================================================
    // 3. CURSOR OFFSET MAPPING TESTS
    // =========================================================================

    @Test
    fun `offset mapping correctly maps original to transformed and transformed to original for RJ-14-CV-0002`() {
        val result = VehicleRegistrationFormatter.format("RJ14CV0002", allowBhSeries = true)
        // raw = "RJ14CV0002" (length 10)
        // formatted = "RJ-14-CV-0002" (length 13)

        // Start of string
        assertEquals(0, result.originalToTransformed(0))
        assertEquals(0, result.transformedToOriginal(0))

        // After 'R' (offset 1)
        assertEquals(1, result.originalToTransformed(1))
        assertEquals(1, result.transformedToOriginal(1))

        // After 'RJ' (offset 2) -> in transformed it is after hyphen (offset 3: "RJ-|")
        assertEquals(3, result.originalToTransformed(2))
        assertEquals(2, result.transformedToOriginal(3))

        // After '14' (offset 4) -> in transformed it is after 2nd hyphen (offset 6: "RJ-14-|")
        assertEquals(6, result.originalToTransformed(4))
        assertEquals(4, result.transformedToOriginal(6))

        // After 'CV' (offset 6) -> in transformed it is after 3rd hyphen (offset 9: "RJ-14-CV-|")
        assertEquals(9, result.originalToTransformed(6))
        assertEquals(6, result.transformedToOriginal(9))

        // End of string (offset 10) -> transformed end (offset 13: "RJ-14-CV-0002|")
        assertEquals(13, result.originalToTransformed(10))
        assertEquals(10, result.transformedToOriginal(13))
    }

    @Test
    fun `offset mapping handles trailing hyphen when typing incomplete segments`() {
        val result = VehicleRegistrationFormatter.format("RJ", allowBhSeries = true)
        // raw = "RJ" (len 2), formatted = "RJ-" (len 3)
        assertEquals(3, result.originalToTransformed(2)) // Cursor positioned after '-'
        assertEquals(2, result.transformedToOriginal(3))
    }

    @Test
    fun `offset mapping out-of-bounds inputs are safely clamped`() {
        val result = VehicleRegistrationFormatter.format("RJ14", allowBhSeries = true)
        assertEquals(0, result.originalToTransformed(-5))
        assertEquals(6, result.originalToTransformed(100))
        assertEquals(0, result.transformedToOriginal(-10))
        assertEquals(4, result.transformedToOriginal(200))
    }

    // =========================================================================
    // 4. BACKSPACE & PASTED STRING TESTS
    // =========================================================================

    @Test
    fun `backspace deletion progression smoothly unwinds hyphens without getting stuck`() {
        var raw = "RJ14CV0002"
        while (raw.isNotEmpty()) {
            val formatted = VehicleRegistrationFormatter.format(raw, allowBhSeries = true)
            // Cursor at end of raw is always at end of formatted
            assertEquals(formatted.formatted.length, formatted.originalToTransformed(raw.length))
            // Deleting 1 character removes the last raw char
            raw = raw.dropLast(1)
        }
        val empty = VehicleRegistrationFormatter.format("", allowBhSeries = true)
        assertEquals("", empty.formatted)
    }

    @Test
    fun `pasted formatted or spaced strings normalize and format cleanly`() {
        val pastedWithHyphens = "rj-14-cv-0002"
        val formatted1 = VehicleRegistrationFormatter.format(pastedWithHyphens, allowBhSeries = true)
        assertEquals("RJ-14-CV-0002", formatted1.formatted)
        assertEquals("RJ14CV0002", formatted1.raw)

        val pastedWithSpaces = "RJ 14 CV 0002"
        val formatted2 = VehicleRegistrationFormatter.format(pastedWithSpaces, allowBhSeries = true)
        assertEquals("RJ-14-CV-0002", formatted2.formatted)
        assertEquals("RJ14CV0002", formatted2.raw)
    }

    @Test
    fun `leading zeros in RTO district and registration number are preserved`() {
        val result = VehicleRegistrationFormatter.format("DL01S0005", allowBhSeries = true)
        assertEquals("DL-01-S-0005", result.formatted)
        assertEquals("01", result.segments.seg2)
        assertEquals("0005", result.segments.seg4)
    }
}

