package com.policyboss.policybosspro.core.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class NetworkErrorAndConnectivityTest {

    // =========================================================================
    // 1. NETWORK ERROR CLASSIFICATION TESTS
    // =========================================================================

    @Test
    fun testNetworkError_noInternetClassification() {
        val unknownHost = UnknownHostException("Unable to resolve host")
        val connectEx = ConnectException("Failed to connect to server")
        val ioOffline = IOException("Network connection is offline")

        assertTrue(NetworkError.fromThrowable(unknownHost) is NetworkError.NoInternet)
        assertTrue(NetworkError.fromThrowable(connectEx) is NetworkError.NoInternet)
        assertTrue(NetworkError.fromThrowable(ioOffline) is NetworkError.NoInternet)
    }

    @Test
    fun testNetworkError_timeoutClassification() {
        val socketTimeout = SocketTimeoutException("Read timed out")
        val err = NetworkError.fromThrowable(socketTimeout)

        assertTrue(err is NetworkError.Timeout)
        assertEquals("Connection timed out. Please try again.", err.errorMessage)
    }

    @Test
    fun testNetworkError_httpErrorsClassification() {
        val unauthorizedResponse = Response.error<Any>(401, "Unauthorized".toResponseBody())
        val unauthorizedHttp = HttpException(unauthorizedResponse)
        val unauthorizedErr = NetworkError.fromThrowable(unauthorizedHttp)
        assertTrue(unauthorizedErr is NetworkError.Unauthorized)

        val serverResponse = Response.error<Any>(503, "Service Unavailable".toResponseBody())
        val serverHttp = HttpException(serverResponse)
        val serverErr = NetworkError.fromThrowable(serverHttp)
        assertTrue(serverErr is NetworkError.ServerError)
        assertEquals(503, (serverErr as NetworkError.ServerError).code)
    }

    @Test
    fun testNetworkError_parsingErrorClassification() {
        val serializationEx = kotlinx.serialization.SerializationException("Unexpected token")
        val err = NetworkError.fromThrowable(serializationEx)
        assertTrue(err is NetworkError.ParsingError)
    }

    // =========================================================================
    // 2. CONNECTIVITY OBSERVER MOCK CONTRACT TEST
    // =========================================================================

    @Test
    fun testConnectivityObserverContract() {
        val mockObserver = object : ConnectivityObserver {
            private var connected = false
            override val isConnected: Flow<Boolean> = flowOf(connected)
            override fun isCurrentlyConnected(): Boolean = connected
            fun setConnected(value: Boolean) { connected = value }
        }

        assertFalse(mockObserver.isCurrentlyConnected())
        mockObserver.setConnected(true)
        assertTrue(mockObserver.isCurrentlyConnected())
    }
}

