package com.policyboss.policybosspro.feature.carInsurance.data.repository

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Supplies the dynamic `session_id` sent with `quote/vehicle_info_user`.
 *
 * The session id changes at runtime, so it is never stored in BuildConfig or source.
 * Its real origin (login response, a session API, etc.) is still to be decided: whichever
 * flow obtains it calls [updateSessionId], and the repository reads it per request.
 * Until then an empty session id is sent.
 */
@Singleton
class VehicleInfoSessionProvider @Inject constructor() {

    @Volatile
    private var sessionId: String = ""

    fun getSessionId(): String = sessionId

    fun updateSessionId(newSessionId: String?) {
        sessionId = newSessionId?.trim().orEmpty()
    }

    fun clear() {
        sessionId = ""
    }
}
