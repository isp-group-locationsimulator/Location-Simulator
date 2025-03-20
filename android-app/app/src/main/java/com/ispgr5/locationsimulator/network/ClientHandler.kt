package com.ispgr5.locationsimulator.network

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.MutableLiveData
import com.ispgr5.locationsimulator.presentation.trainerScreen.Device
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.seconds

class ObservableDeviceList {
    private val deviceList = MutableLiveData<ArrayList<Device>>()

    @Composable
    fun observeAsState(): State<ArrayList<Device>?> {
        return deviceList.observeAsState()
    }

    fun getAsList(): List<Device> {
        return deviceList.value ?: emptyList()
    }

    fun addDevice(device: Device) {
        val newList = ArrayList<Device>()
        newList.addAll(deviceList.value ?: emptyList())
        newList.add(device)
        deviceList.postValue(newList)
    }

    fun removeDevice(user: String) {
        val newList = ArrayList<Device>()
        for (dev in deviceList.value ?: emptyList()) {
            if (dev.user != user) {
                newList.add(dev)
            }
        }
        deviceList.postValue(newList)
    }

    fun updateDevice(device: Device) {
        var changed = false
        val newList = ArrayList<Device>()

        newList.addAll(deviceList.value ?: emptyList())
        for (i in 0..<newList.size) {
            if (newList[i].user == device.user) {
                changed = newList[i].isPlaying != device.isPlaying || newList[i].isConnected != device.isConnected || newList[i].selectedConfig != device.selectedConfig
                newList[i] = device.copy()
            }
        }
        if (changed) {
            deviceList.postValue(newList)
        }
    }
}

class ClientHandler(
    private val name: String,
    private val socket: Socket,
    private val reader: BufferedReader,
    private val writer: BufferedWriter,
) : Thread() {
    private val timeoutChecker = TimeoutChecker(10.seconds)

    companion object {
        val clientHandlers: HashMap<String, ClientHandler> = HashMap()
        val deviceList = ObservableDeviceList()
        private val isCheckConnectionActive = AtomicBoolean(false)

        fun startCheckConnection() {
            isCheckConnectionActive.set(true)
            thread {
                while (isCheckConnectionActive.get()) {
                    sleep(3000)
                    for (device in deviceList.getAsList()) {
                        if (device.isConnected != isConnected(device.user)) {
                            val modifiedDevice = device.copy()
                            modifiedDevice.isConnected = !device.isConnected
                            deviceList.updateDevice(modifiedDevice)
                        }
                    }
                }
            }
        }

        fun stopCheckConnection() {
            isCheckConnectionActive.set(false)
        }

        fun sendToClient(name: String, message: String) {
            println("Server to $name: $message")
            clientHandlers[name]?.send(message)
        }

        fun sendToAllClients(message: String) {
            for ((_, handler) in clientHandlers) {
                handler.send(message)
            }
        }

        fun closeAllClientHandlers() {
            for ((_, clientHandler) in clientHandlers) {
                clientHandler.close()
            }
        }

        private fun isConnected(name: String): Boolean {
           return clientHandlers[name]?.timeoutChecker?.isNotTimedOut() ?: false
        }
    }

    init {
        var wasConnected = false
        for (device in deviceList.getAsList()) {
            if(name == device.user) {
                if(device.isConnected) {
                    socket.close()
                    reader.close()
                    writer.close()
                    throw RuntimeException("Name $name is already taken")
                }
                else {
                    wasConnected = true
                }
            }
        }

        send("Success")
        send("locationSimulatorPing")
        timeoutChecker.startTimer()
        clientHandlers[name] = this

        if(!wasConnected) {
            deviceList.addDevice(
                Device(
                    user = name,
                    name = "Google Pixel",
                    isPlaying = false,
                    isConnected = true
                )
            )
        }
    }

    private fun pingReceived() {
        timeoutChecker.startTimer()
        thread {
            sleep(3000)
            try {
                writer.write("locationSimulatorPing")
                writer.newLine()
                writer.flush()
            } catch (_: Exception) {
            }
        }
    }

    private fun deviceStarted() {
        for(device in deviceList.getAsList()) {
            if(device.user == name && !device.isPlaying) {
                val modifiedDevice = device.copy()
                modifiedDevice.isPlaying = true
                deviceList.updateDevice(modifiedDevice)
            }
        }
    }

    private fun deviceStopped() {
        for(device in deviceList.getAsList()) {
            if(device.user == name && device.isPlaying) {
                val modifiedDevice = device.copy()
                modifiedDevice.isPlaying = false
                deviceList.updateDevice(modifiedDevice)
            }
        }
    }

    private fun parseMessage(message: String) {
        when (message) {
            "locationSimulatorPing" -> pingReceived()
            "localStart" -> deviceStarted()
            "localStop" -> deviceStopped()
            else -> println("Unknown message")
        }
    }

    override fun run() {
        while (!socket.isClosed) {
            try {
                val line = reader.readLine() ?: break
                println("Server received message: $line")
                parseMessage(line)
            } catch (e: IOException) {
                println("Server unable to read line: $e")
                sleep(3000)
            }
        }
    }

    fun send(message: String) {
        thread {
            try {
                writer.write(message)
                writer.newLine()
                writer.flush()
            } catch (e: Exception) {
                println("Server unable to send message: $e")
            }
        }
    }

    fun close() {
        socket.close()
        reader.close()
        writer.close()
        clientHandlers.remove(name)
        deviceList.removeDevice(name)
    }
}