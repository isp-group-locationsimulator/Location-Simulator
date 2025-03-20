package com.ispgr5.locationsimulator.network

import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Collections
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.TimeSource

fun getIPAddress(): String? {
    try {
        val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (interf in interfaces) {
            val addresses = Collections.list(interf.inetAddresses)
            for (address in addresses) {
                if (!address.isLoopbackAddress && address is Inet4Address) {
                    return address.hostAddress?.uppercase(Locale.getDefault())
                }
            }
        }
    } catch (e: SocketException) {
        println("Unable to get IPAddress: $e")
        return null
    }
    println("Unable to get IPAddress: No IPV4 address found")
    return null
}

class TimeoutChecker(
    private val timeoutDuration: Duration
) {
    private val timeSource = TimeSource.Monotonic
    private var timeoutForPing = timeSource.markNow()

    fun startTimer() {
        timeoutForPing = timeSource.markNow() + timeoutDuration
    }

    fun isNotTimedOut(): Boolean {
        return timeoutForPing.hasNotPassedNow()
    }
}