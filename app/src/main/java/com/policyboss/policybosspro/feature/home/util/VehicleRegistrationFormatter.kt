package com.policyboss.policybosspro.feature.home.util

/**
 * Pure Kotlin, Compose-independent utility for normalizing, segmenting,
 * formatting Indian vehicle registration numbers, and calculating cursor offset mappings.
 *
 * Supports:
 * - Standard Indian RTO Format: `SS-RR-SERIES-NNNN` (e.g. `RJ-14-CV-0002`, `MH-02-AB-1234`, `DL-01-S-5678`, `MH-01-ABC-1234`)
 * - Bharat (BH) Series: `YY-BH-NNNN-XX` (e.g. `22-BH-1234-AA`, `22-BH-1234-A`)
 */
object VehicleRegistrationFormatter {

    /**
     * Parsed representation of vehicle registration segments.
     */
    data class Segments(
        val seg1: String, // State (e.g. "RJ") or Year (e.g. "22")
        val seg2: String, // RTO (e.g. "14") or "BH"
        val seg3: String, // Series (e.g. "CV") or Number (e.g. "1234")
        val seg4: String, // Number (e.g. "0002") or Series (e.g. "AA")
        val isBh: Boolean
    )

    /**
     * Holds the formatted string with hyphens alongside pre-computed coordinate lookup tables
     * for cursor mapping.
     */
    data class FormattedRegistration(
        val raw: String,
        val formatted: String,
        val origToTrans: IntArray,
        val transToOrig: IntArray,
        val segments: Segments
    ) {
        fun originalToTransformed(offset: Int): Int {
            if (origToTrans.isEmpty()) return 0
            val safe = offset.coerceIn(0, origToTrans.size - 1)
            return origToTrans[safe].coerceIn(0, formatted.length)
        }

        fun transformedToOriginal(offset: Int): Int {
            if (transToOrig.isEmpty()) return 0
            val safe = offset.coerceIn(0, transToOrig.size - 1)
            return transToOrig[safe].coerceIn(0, raw.length)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as FormattedRegistration
            return raw == other.raw &&
                    formatted == other.formatted &&
                    origToTrans.contentEquals(other.origToTrans) &&
                    transToOrig.contentEquals(other.transToOrig) &&
                    segments == other.segments
        }

        override fun hashCode(): Int {
            var result = raw.hashCode()
            result = 31 * result + formatted.hashCode()
            result = 31 * result + origToTrans.contentHashCode()
            result = 31 * result + transToOrig.contentHashCode()
            result = 31 * result + segments.hashCode()
            return result
        }
    }

    /**
     * Cleans and normalizes input to uppercase alphanumeric characters without separators.
     */
    fun clean(input: String): String {
        return input.uppercase().filter { it.isLetterOrDigit() }
    }

    /**
     * Parses raw normalized input into logical segments based on format rules.
     */
    fun parseSegments(raw: String, allowBhSeries: Boolean): Segments {
        val clean = clean(raw)
        if (clean.isEmpty()) {
            return Segments(seg1 = "", seg2 = "", seg3 = "", seg4 = "", isBh = false)
        }

        val isBh = allowBhSeries && clean.first().isDigit()

        return if (isBh) {
            // BH Series: YY-BH-NNNN-XX
            val year = clean.take(2).filter { it.isDigit() }
            val remainingAfterYear = if (clean.length > year.length) clean.substring(year.length) else ""

            val bh = remainingAfterYear.take(2).filter { it.isLetter() }
            val remainingAfterBh = if (remainingAfterYear.length > bh.length) remainingAfterYear.substring(bh.length) else ""

            val number = remainingAfterBh.take(4).filter { it.isDigit() }
            val remainingAfterNumber = if (remainingAfterBh.length > number.length) remainingAfterBh.substring(number.length) else ""

            val series = remainingAfterNumber.take(2).filter { it.isLetter() }

            Segments(
                seg1 = year,
                seg2 = bh,
                seg3 = number,
                seg4 = series,
                isBh = true
            )
        } else {
            // Standard RTO: SS-RR-SERIES-NNNN
            // 1. State code: up to 2 letters
            var stateEnd = 0
            while (stateEnd < clean.length && stateEnd < 2 && clean[stateEnd].isLetter()) {
                stateEnd++
            }
            val state = clean.substring(0, stateEnd)

            // 2. RTO code: up to 2 digits
            var rtoEnd = stateEnd
            while (rtoEnd < clean.length && rtoEnd < stateEnd + 2 && clean[rtoEnd].isDigit()) {
                rtoEnd++
            }
            val rto = clean.substring(stateEnd, rtoEnd)

            // 3. Series letters: up to 3 letters
            var seriesEnd = rtoEnd
            while (seriesEnd < clean.length && seriesEnd < rtoEnd + 3 && clean[seriesEnd].isLetter()) {
                seriesEnd++
            }
            val series = clean.substring(rtoEnd, seriesEnd)

            // 4. Number block: up to 4 digits
            var numberEnd = seriesEnd
            while (numberEnd < clean.length && numberEnd < seriesEnd + 4 && clean[numberEnd].isDigit()) {
                numberEnd++
            }
            val number = clean.substring(seriesEnd, numberEnd)

            Segments(
                seg1 = state,
                seg2 = rto,
                seg3 = series,
                seg4 = number,
                isBh = false
            )
        }
    }

    /**
     * Formats raw vehicle registration string with hyphens and computes bidirectional cursor maps.
     *
     * @param raw Clean or unformatted vehicle registration string
     * @param allowBhSeries Whether BH series format is supported (true for Car & TW, false for CV)
     * @param isSeriesClosed True if series is explicitly closed (via Next action or hyphen input)
     */
    fun format(
        raw: String,
        allowBhSeries: Boolean,
        isSeriesClosed: Boolean = false
    ): FormattedRegistration {
        val clean = clean(raw)
        if (clean.isEmpty()) {
            val emptySegments = Segments("", "", "", "", false)
            return FormattedRegistration(
                raw = "",
                formatted = "",
                origToTrans = intArrayOf(0),
                transToOrig = intArrayOf(0),
                segments = emptySegments
            )
        }

        val segments = parseSegments(clean, allowBhSeries)
        val formatted = buildString {
            if (segments.isBh) {
                // BH Series: YY-BH-NNNN-XX
                append(segments.seg1) // Year
                if (segments.seg1.length == 2) {
                    append('-')
                }

                if (segments.seg2.isNotEmpty()) {
                    append(segments.seg2) // BH
                    if (segments.seg2.length == 2) {
                        append('-')
                    }
                }

                if (segments.seg3.isNotEmpty()) {
                    append(segments.seg3) // NNNN
                    if (segments.seg3.length == 4) {
                        append('-')
                    }
                }

                if (segments.seg4.isNotEmpty()) {
                    append(segments.seg4) // XX
                }
            } else {
                // Standard RTO: SS-RR-SERIES-NNNN
                append(segments.seg1) // State
                if (segments.seg1.length == 2) {
                    append('-')
                }

                if (segments.seg2.isNotEmpty()) {
                    append(segments.seg2) // RTO
                    if (segments.seg2.length == 2) {
                        append('-')
                    }
                }

                if (segments.seg3.isNotEmpty()) {
                    append(segments.seg3) // Series
                    val shouldHyphenateSeries = when {
                        segments.seg4.isNotEmpty() -> true // Number block started (e.g. DL-01-S-5)
                        segments.seg3.length == 2 -> true  // Common 2-letter series auto-hyphens (e.g. RJ-14-CV-)
                        segments.seg3.length == 3 -> true  // 3-letter series auto-hyphens (e.g. MH-01-ABC-)
                        segments.seg3.length == 1 && isSeriesClosed -> true // User closed 1-letter series (e.g. DL-01-S-)
                        else -> false
                    }
                    if (shouldHyphenateSeries) {
                        append('-')
                    }
                }

                if (segments.seg4.isNotEmpty()) {
                    append(segments.seg4) // Number block (up to 4 digits, preserves leading zeros)
                }
            }
        }

        // Compute bidirectional offset tables
        val n = clean.length
        val m = formatted.length
        val origToTrans = IntArray(n + 1)
        val transToOrig = IntArray(m + 1)

        var cleanIdx = 0
        origToTrans[0] = 0
        transToOrig[0] = 0

        for (formIdx in 0 until m) {
            if (formatted[formIdx] == '-') {
                // Hyphen position: cursor after hyphen maps to cleanIdx in raw text
                transToOrig[formIdx + 1] = cleanIdx
                origToTrans[cleanIdx] = formIdx + 1
            } else {
                if (cleanIdx < n && formatted[formIdx] == clean[cleanIdx]) {
                    cleanIdx++
                    origToTrans[cleanIdx] = formIdx + 1
                    transToOrig[formIdx + 1] = cleanIdx
                }
            }
        }

        // If formatted ends with a hyphen (e.g. "RJ-", "RJ-14-", "RJ-14-CV-"),
        // the cursor at the end of raw text (n) must map to the end of formatted (m)
        origToTrans[n] = m

        // Ensure monotonicity
        for (i in 1..m) {
            if (transToOrig[i] < transToOrig[i - 1]) {
                transToOrig[i] = transToOrig[i - 1]
            }
        }
        for (i in 1..n) {
            if (origToTrans[i] < origToTrans[i - 1]) {
                origToTrans[i] = origToTrans[i - 1]
            }
        }

        return FormattedRegistration(
            raw = clean,
            formatted = formatted,
            origToTrans = origToTrans,
            transToOrig = transToOrig,
            segments = segments
        )
    }
}
