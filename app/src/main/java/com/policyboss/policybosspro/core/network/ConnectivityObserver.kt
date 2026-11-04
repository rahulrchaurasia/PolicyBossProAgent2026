package com.policyboss.policybosspro.core.network

import kotlinx.coroutines.flow.Flow

/**
 * Enterprise-grade contract for observing real-time network connectivity.
 */
interface ConnectivityObserver {

    /**
     * Cold/Hot stream emitting true when the active network has validated internet access,
     * and false when disconnected or captive.
     * Guaranteed to emit current status immediately and avoid redundant duplicate emissions.
     */
    val isConnected: Flow<Boolean>

    /**
     * Synchronous snapshot check to verify if the active network currently has validated internet capability.
     */
    fun isCurrentlyConnected(): Boolean
}

