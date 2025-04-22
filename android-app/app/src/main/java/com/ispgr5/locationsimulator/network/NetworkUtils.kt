package com.ispgr5.locationsimulator.network

import android.util.Log
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

private const val TAG = "NetworkUtils"

/**
 * All commands that can be used by the networking classes
 */
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

/**
 * Get the IPv4-Address for this device
 *
 * @return the IP-Address or null on error
 */
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
        Log.w(TAG, "Unable to get IPAddress: $e")
        return null
    }
    Log.w(TAG, "Unable to get IPAddress: No IPV4 address found")
    return null
}

/**
 * Used to validate the name of a remote device
 *
 * @return true if the name is valid
 */
fun validateRemoteName(name: String): Boolean {
    return name.isNotEmpty() && name.all { !it.isWhitespace() }
}

/**
 * Can be used to check whether a certain amount of time has passed
 */
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

/**
 * Data structure to store a list of devices.
 * It can also be observed and allows for thread safe modification
 */
class ObservableDeviceList {
    private val deviceList = MutableLiveData<List<Device>>()
    private var internalList = ArrayList<Device>()

    @Composable
    fun observeAsState(): State<List<Device>?> {
        return deviceList.observeAsState()
    }

    /**
     * Get a copy of the device list
     *
     * @return the list of devices
     */
    fun getAsList(): List<Device> {
        val listCopy = ArrayList<Device>()

        synchronized(this) { listCopy.addAll(deviceList.value ?: emptyList()) }

        return listCopy
    }

    /**
     * Remove all devices from the list
     */
    fun clear() {
        synchronized(this) {
            internalList = ArrayList()
            deviceList.postValue(ArrayList())
        }
    }

    /**
     * Updates the device passed as parameter with new data if a device with the same IP-Address is
     * found.
     * If no device with the same IP-Address is found, the list won't be updated
     *
     * @param device the device to modify
     */
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

    /**
     * Updates the device passed as parameter with new data if a device with the same IP-Address is
     * found.
     * If no device with the same IP-Address is found, the device will be inserted as a new device
     *
     * @param device the device to modify or add
     */
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