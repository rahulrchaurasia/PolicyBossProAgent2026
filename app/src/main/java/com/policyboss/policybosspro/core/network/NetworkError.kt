package com.policyboss.policybosspro.core.network

import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Categorized domain network errors ensuring distinct handling between
 * offline states, socket timeouts, server errors, authentication rejections, and data parsing.
 */
sealed class NetworkError(open val errorMessage: String) : Exception(errorMessage) {

    data class NoInternet(
        override val errorMessage: String = "No internet connection. Please check your network settings."
    ) : NetworkError(errorMessage)

    data class Timeout(
        override val errorMessage: String = "Connection timed out. Please try again."
    ) : NetworkError(errorMessage)

    data class ServerError(
        val code: Int,
        override val errorMessage: String = "Server encountered an issue ($code). Please try again later."
    ) : NetworkError(errorMessage)

    data class Unauthorized(
        override val errorMessage: String = "Session expired or unauthorized. Please log in again."
    ) : NetworkError(errorMessage)

    data class ParsingError(
        override val errorMessage: String = "Failed to parse data from server."
    ) : NetworkError(errorMessage)

    data class Unknown(
        override val errorMessage: String = "An unexpected error occurred. Please try again."
    ) : NetworkError(errorMessage)

    companion object {
        fun fromThrowable(throwable: Throwable): NetworkError {
            return when (throwable) {
                is NetworkError -> throwable
                is UnknownHostException, is ConnectException -> NoInternet()
                is SocketTimeoutException -> Timeout()
                is HttpException -> {
                    when (throwable.code()) {
                        401, 403 -> Unauthorized()
                        in 500..599 -> ServerError(throwable.code())
                        else -> ServerError(throwable.code(), "Request failed with HTTP status ${throwable.code()}")
                    }
                }
                is IOException -> {
                    val msg = throwable.message.orEmpty().lowercase()
                    if (msg.contains("network") || msg.contains("connection") || msg.contains("offline") || msg.contains("unreachable")) {
                        NoInternet()
                    } else {
                        Timeout()
                    }
                }
                is kotlinx.serialization.SerializationException -> ParsingError()
                else -> Unknown(throwable.localizedMessage ?: "An unexpected error occurred.")
            }
        }
    }
}

