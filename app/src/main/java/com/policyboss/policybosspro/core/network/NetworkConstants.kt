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
     * Default authentication/authorization header token for Horizon services.
     */
    const val DEFAULT_TOKEN = "1234567890"

    /**
     * Network request timeouts in seconds.
     */
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
}

