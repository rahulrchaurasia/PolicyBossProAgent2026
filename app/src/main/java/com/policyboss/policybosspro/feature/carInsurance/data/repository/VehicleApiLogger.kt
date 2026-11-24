package com.policyboss.policybosspro.feature.carInsurance.data.repository

import android.util.Log
import com.policyboss.policybosspro.BuildConfig

/**
 * Debug-only Logcat logging for the vehicle info / model list APIs.
 * Filter Logcat by tag `VehicleInfoApi`. Nothing is logged in release builds.
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

    // Logcat truncates long lines (~4k), so split large bodies.
    private fun chunked(message: String): List<String> = message.chunked(MAX_CHUNK)
}
