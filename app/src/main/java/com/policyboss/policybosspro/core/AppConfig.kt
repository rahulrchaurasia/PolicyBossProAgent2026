package com.policyboss.policybosspro.core

import com.policyboss.policybosspro.BuildConfig

/**
 * Global application configuration flags.
 * Central source of truth for runtime modes, development flags, and feature toggles.
 */
object AppConfig {

    /**
     * When true, repositories serve comprehensive, realistic dummy data
     * for Home, Leads, and related features without hitting live APIs, and
     * motor quote flows fall back to locally generated sample quotes.
     *
     * Tied to [BuildConfig.DEBUG] so release builds never present mock data
     * (e.g. fake premiums) as real results.
     */
    val USE_DUMMY_DATA: Boolean = BuildConfig.DEBUG
}

