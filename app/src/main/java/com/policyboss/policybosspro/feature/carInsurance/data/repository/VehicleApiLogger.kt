package com.policyboss.policybosspro.feature.carInsurance.data.repository

import android.util.Log
import com.policyboss.policybosspro.BuildConfig

/**
 * Debug-only Logcat logging for the vehicle info / model list APIs.
 * Filter Logcat by tag `VehicleInfoApi`. Credentials are always masked; nothing is
 * logged in release builds.
 */
internal object VehicleApiLogger {
    const val TAG = "VehicleInfoApi"
    private const val MAX_CHUNK = 3500

    fun d(message: String) {
        if (BuildConfig.DEBUG) chunked(message).forEach { Log.d(TAG, it) }
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (!BuildConfig.DEBUG) return
        chunked(message).forEach { Log.e(TAG, it) }
        if (throwable != null) Log.e(TAG, throwable.javaClass.simpleName + ": " + throwable.message, throwable)
    }

    /** Shows only the first 4 and last 3 characters so a key is recognisable but never exposed. */
    fun mask(value: String): String = when {
        value.isBlank() -> "<empty>"
        value.length <= 8 -> "****"
        else -> value.take(4) + "…" + value.takeLast(3)
    }

    // Logcat truncates long lines (~4k), so split large bodies.
    private fun chunked(message: String): List<String> = message.chunked(MAX_CHUNK)
}
