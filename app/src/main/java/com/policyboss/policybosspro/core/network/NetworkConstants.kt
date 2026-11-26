package com.policyboss.policybosspro.core.network

/**
 * Global Network Constants for API communication across PolicyBoss Pro.
 * Single source of truth for base URLs, tokens, and network configurations.
 */
object NetworkConstants {

    /**
     * Horizon Base API URL.
     * Note: Retrofit requires the base URL to end with a trailing slash '/'.
     */
    const val BASE_URL = "https://horizon.policyboss.com:5443/"

    /**
     * Quote / vehicle API host. horizon.policyboss.com permanently redirects (HTTP 308)
     * these endpoints here, so they are called directly to avoid the extra hop.
     */
    const val QUOTE_API_BASE_URL = "https://api.policyboss.com:5443/"

    /**
     * Default authentication/authorization header token for Horizon services.
     */
    const val DEFAULT_TOKEN = "1234567890"

    // =========================================================================
    // Vehicle & Quote Security Credentials (Shared across Quote & Vahan APIs)
    // =========================================================================
    /**
     * Fixed secret key for PolicyBoss vehicle & quote lookup services.
     */
    const val SECRET_KEY = "SECRET-HZ07QRWY-JIBT-XRMQ-ZP95-J0RWP3DYRACW"

    /**
     * Fixed client key for PolicyBoss vehicle & quote lookup services.
     */
    const val CLIENT_KEY = "CLIENT-CNTP6NYE-CU9N-DUZW-CSPI-SH1IS4DOVHB9"

    /**
     * Primary active SESSION_ID used across all quote request bodies.
     * Easily modify this value here whenever a new daily/session key is issued.
     */
    const val SESSION_ID = "-HFNrqwMtteX9alZq9lc1OBqU-h0Wv35"

    /**
     * Network request timeouts in seconds.
     */
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
}

