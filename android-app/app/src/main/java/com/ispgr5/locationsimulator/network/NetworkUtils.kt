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
    const val TIMER_STATE = "TIMER_STATE"
    const val IS_PLAYING = "IS_PLAYING"
    const val IS_IDLE = "IS_IDLE"

    fun formatBroadcast(ipAddress: String, name: String): String {
        return "$BROADCAST $ipAddress $name"
    }

    fun formatStart(config: String, hours: Long = 0, minutes: Long = 0, seconds: Long = 0): String {
        return "$START $hours:$minutes:$seconds $config"
    }

    fun timerStateToString(hours: Long = 0, minutes: Long = 0, seconds: Long = 0): String {
        return "$hours:$minutes:$seconds"
    }

    fun formatTimerState(hours: Long = 0, minutes: Long = 0, seconds: Long = 0): String {
        return "$TIMER_STATE ${timerStateToString(hours, minutes, seconds)}"
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

fun validateRemoteName(name: String): Boolean {
    return name.isNotEmpty() && name.all { !it.isWhitespace() }
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
    private val deviceList = MutableLiveData<List<Device>>()
    private var internalList = ArrayList<Device>()

    @Composable
    fun observeAsState(): State<List<Device>?> {
        return deviceList.observeAsState()
    }

    fun getAsList(): List<Device> {
        val listCopy = ArrayList<Device>()

        synchronized(this) { listCopy.addAll(deviceList.value ?: emptyList()) }

        return listCopy
    }

    fun clear() {
        synchronized(this) {
            internalList = ArrayList()
            deviceList.postValue(ArrayList())
        }
    }

    fun updateDevice(device: Device) {
        synchronized(this) {
            val newInternalList = ArrayList<Device>()
            newInternalList.addAll(internalList)

            for (i in 0..<newInternalList.size) {
                if (newInternalList[i].ipAddress == device.ipAddress) {
                    if (newInternalList[i].user != device.user ||
                        newInternalList[i].timerState != device.timerState ||
                        newInternalList[i].isPlaying != device.isPlaying ||
                        newInternalList[i].isConnected != device.isConnected ||
                        newInternalList[i].selectedConfig != device.selectedConfig
                    ) {
                        newInternalList[i] = device.copy()
                        internalList = newInternalList
                        deviceList.postValue(internalList)
                        break
                    }
                }
            }
        }
    }

    fun updateOrAddDevice(device: Device) {
        synchronized(this) {
            var changed = false
            var exists = false
            val newInternalList = ArrayList<Device>()
            newInternalList.addAll(internalList)

            for (i in 0..<newInternalList.size) {
                if (newInternalList[i].ipAddress == device.ipAddress) {
                    changed =
                        newInternalList[i].user != device.user || newInternalList[i].isPlaying != device.isPlaying || newInternalList[i].isConnected != device.isConnected || newInternalList[i].selectedConfig != device.selectedConfig
                    exists = true
                    newInternalList[i] = device.copy()
                }
            }
            if (!exists) {
                newInternalList.add(device)
                changed = true
            }
            if (changed) {
                internalList = newInternalList
                deviceList.postValue(internalList)
            }
        }
    }
}