package com.policyboss.policybosspro.feature.carInsurance.data.repository

import com.policyboss.policybosspro.core.network.NetworkConstants
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Optional runtime override for the `session_id` sent with `quote/vehicle_info_user`.
 *
 * When nothing has been set, the request uses the default centralized
 * [NetworkConstants.SESSION_ID]. If the session id is dynamically updated at runtime,
 * calling [updateSessionId] ensures the repository and request bodies send it instead.
 */
@Singleton
class VehicleInfoSessionProvider @Inject constructor() {

    @Volatile
    private var sessionId: String = ""

    fun getSessionId(): String = sessionId.ifBlank { NetworkConstants.SESSION_ID }

    fun updateSessionId(newSessionId: String?) {
        sessionId = newSessionId?.trim().orEmpty()
    }

    fun clear() {
        sessionId = ""
    }
}
