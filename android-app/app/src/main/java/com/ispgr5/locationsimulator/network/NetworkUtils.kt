package com.ispgr5.locationsimulator.network

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.MutableLiveData
import com.ispgr5.locationsimulator.presentation.trainerScreen.Device
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Collections
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.TimeSource

data object Commands {
    const val BROADCAST = "LOCATION_SIMULATOR_BROADCAST"
    const val PING = "LOCATION_SIMULATOR_PING"
    const val START = "START"
    const val STOP = "STOP"
    const val LOCAL_START = "LOCAL_START"
    const val LOCAL_STOP = "LOCAL_STOP"

    fun formatBroadcast(ipAddress: String, name: String): String {
        return "$BROADCAST $ipAddress $name"
    }

    fun formatStart(config: String, hours: Long = 0, minutes: Long = 0, seconds: Long = 0): String {
        return "$START $hours:$minutes:$seconds $config"
    }
}

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

    fun isTimedOut(): Boolean {
        return timeoutForPing.hasPassedNow()
    }
}

class ObservableDeviceList {
    private val deviceList = MutableLiveData<ArrayList<Device>>()

    @Composable
    fun observeAsState(): State<ArrayList<Device>?> {
        return deviceList.observeAsState()
    }

    fun getAsList(): List<Device> {
        return deviceList.value ?: emptyList()
    }

    fun clear() {
        deviceList.value?.clear()
    }

    fun updateDevice(device: Device) {
        var changed = false
        var exists = false
        val newList = ArrayList<Device>()

        newList.addAll(deviceList.value ?: emptyList())
        for (i in 0..<newList.size) {
            if (newList[i].ipAddress == device.ipAddress) {
                changed =
                    newList[i].user != device.user || newList[i].isPlaying != device.isPlaying || newList[i].isConnected != device.isConnected || newList[i].selectedConfig != device.selectedConfig
                exists = true
                newList[i] = device.copy()
            }
        }
        if (!exists) {
            newList.add(device)
            changed = true
        }
        if (changed) {
            deviceList.postValue(newList)
        }
    }

    fun updateDevices(devices: List<Device>) {
        var changed = false
        val newList = ArrayList<Device>()
        newList.addAll(deviceList.value ?: emptyList())

        for (device in devices) {
            var exists = false
            for (i in 0..<newList.size) {
                if (newList[i].ipAddress == device.ipAddress) {
                    changed =
                        newList[i].user != device.user || newList[i].isPlaying != device.isPlaying || newList[i].isConnected != device.isConnected || newList[i].selectedConfig != device.selectedConfig
                    exists = true
                    newList[i] = device.copy()
                }
            }
            if (!exists) {
                newList.add(device)
                changed = true
            }
        }
        if (changed) {
            deviceList.postValue(newList)
        }
    }
}