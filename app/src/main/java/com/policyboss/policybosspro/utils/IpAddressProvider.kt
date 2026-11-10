package com.policyboss.policybosspro.utils

import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.Collections
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IpAddressProvider @Inject constructor() {

    /**
     * Safely retrieves the device's current IPv4 address from active network interfaces (Wi-Fi, Cellular, Ethernet, VPN).
     * Falls back to [FALLBACK_IP] ("0.0.0.0") if no active address is resolved or in case of network exceptions.
     */
    fun getIpAddress(): String {
        return getDeviceIpAddress()
    }

    companion object {
        const val FALLBACK_IP = "0.0.0.0"

        /**
         * Standalone utility method to safely extract the active device IP address without requiring DI.
         */
        fun getDeviceIpAddress(preferIPv4: Boolean = true): String {
            return try {
                val interfaces = NetworkInterface.getNetworkInterfaces() ?: return FALLBACK_IP
                var ipv6Fallback: String? = null

                for (networkInterface in Collections.list(interfaces)) {
                    // Skip loopback and down/inactive interfaces
                    if (!networkInterface.isUp || networkInterface.isLoopback) continue

                    val addresses = networkInterface.inetAddresses ?: continue
                    for (inetAddress in Collections.list(addresses)) {
                        if (inetAddress.isLoopbackAddress) continue

                        val hostAddress = inetAddress.hostAddress ?: continue
                        val isIPv4 = inetAddress is Inet4Address || !hostAddress.contains(':')

                        if (isIPv4) {
                            if (preferIPv4 && hostAddress.isNotBlank()) {
                                return hostAddress
                            }
                        } else {
                            if (ipv6Fallback == null && hostAddress.isNotBlank()) {
                                val delim = hostAddress.indexOf('%') // Strip zone index if present
                                ipv6Fallback = if (delim < 0) hostAddress.uppercase() else hostAddress.substring(0, delim).uppercase()
                            }
                        }
                    }
                }

                // If IPv4 was preferred but not found, fallback to IPv6 if present, else default
                ipv6Fallback ?: FALLBACK_IP
            } catch (_: Throwable) {
                FALLBACK_IP
            }
        }
    }
}

