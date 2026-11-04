package com.policyboss.policybosspro.core

/**
 * Global application configuration flags.
 * Central source of truth for runtime modes, development flags, and feature toggles.
 */
object AppConfig {

    /**
     * When true, repositories serve comprehensive, realistic dummy data
     * for Home, Leads, and related features without hitting live APIs.
     * Switch to false to route requests to actual backend Retrofit services.
     */
    const val USE_DUMMY_DATA: Boolean = true
}

