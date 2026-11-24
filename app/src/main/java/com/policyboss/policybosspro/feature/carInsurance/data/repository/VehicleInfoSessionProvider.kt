package com.policyboss.policybosspro.feature.carInsurance.data.repository

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Optional runtime override for the `session_id` sent with `quote/vehicle_info_user`.
 *
 * When nothing has been set, the request uses the constant
 * VehicleInfoRequest.SESSION_ID. If the session id later comes from an API response,
 * that flow calls [updateSessionId] and the repository sends it instead.
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
