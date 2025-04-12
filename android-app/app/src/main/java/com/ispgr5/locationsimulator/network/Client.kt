package com.ispgr5.locationsimulator.network

import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.MulticastLock
import com.ispgr5.locationsimulator.presentation.trainerScreen.Device
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Thread.sleep
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.MulticastSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.seconds


object ClientSingleton {
    enum class ActiveState {
        IS_CLOSED, IS_CLOSING, IS_STARTED, IS_STARTING
    }

    var wifiManager: WifiManager? = null

    val deviceList = ObservableDeviceList()
    val clients = ConcurrentHashMap<String, Client>()

    private var lock: MulticastLock? = null
    private var connectionClient: ConnectionClient? = null
    private val isCheckConnectionActive = AtomicBoolean(false)
    private val activeState = AtomicReference<ActiveState>(ActiveState.IS_CLOSED)

    fun start(): Boolean {
        if(activeState.get() != ActiveState.IS_CLOSED) {
            return false
        }
        activeState.set(ActiveState.IS_STARTING)

        if (wifiManager == null) {
             println("WifiManager missing")
             activeState.set(ActiveState.IS_CLOSED)
             return false
        }

        lock = wifiManager?.createMulticastLock("ClientLock")
        if (lock == null) {
            println("Unable to create multicast lock")
            activeState.set(ActiveState.IS_CLOSED)
            return false
        }

        try {
            lock!!.acquire()

            connectionClient = ConnectionClient()
            connectionClient?.start()
            startCheckConnection()
        } catch (e: Exception) {
            println("Unable to start connection client: $e")
            close()
            return false
        }
        activeState.set(ActiveState.IS_STARTED)
        return true
    }

    fun send(ipAddress: String, message: String) {
        clients[ipAddress]?.send(message)
    }

    fun close() {
        if(activeState.get() != ActiveState.IS_STARTING && activeState.get() != ActiveState.IS_STARTED) {
            println("ClientSingleton is already shut down or shutting down")
            return
        }
        activeState.set(ActiveState.IS_CLOSING)

        connectionClient?.close()
        lock?.release()
        for ((_, client) in clients) {
            client.close()
        }
        clients.clear()
        deviceList.clear()
        stopCheckConnection()
    }

    private fun startCheckConnection() {
        isCheckConnectionActive.set(true)
        thread {
            while (isCheckConnectionActive.get()) {
                sleep(3000)
                for ((ipAddress, client) in clients) {
                    if (client.timeoutChecker.isTimedOut()) {
                        client.close()
                        for (device in deviceList.getAsList()) {
                            if (device.ipAddress == ipAddress) {
                                val modifiedDevice = device.copy()
                                modifiedDevice.isConnected = false
                                deviceList.updateDevice(modifiedDevice)
                                break
                            }
                        }
                    }
                }
            }
            activeState.set(ActiveState.IS_CLOSED)
        }
    }

    private fun stopCheckConnection() {
        if(!isCheckConnectionActive.get()) {
            activeState.set(ActiveState.IS_CLOSED)
        }
        isCheckConnectionActive.set(false)
    }
}

private class ConnectionClient : Thread() {
    private val multicastSocket: MulticastSocket = MulticastSocket(4445)
    private val inetSocketAddress = InetSocketAddress("230.0.0.0", 4445)

    init {
        multicastSocket.joinGroup(inetSocketAddress, null)
    }

    override fun run() {
        val buf = ByteArray(256)
        val packet = DatagramPacket(buf, buf.size)

        while (true) {
            try {
                multicastSocket.receive(packet)
            } catch (e: IOException) {
                println("Client unable to receive multicast packet: $e")
                break
            }

            val received = String(
                packet.data, 0, packet.length
            )
            val split = received.split(' ')
            if (split.count() != 3 || split[0] != Commands.BROADCAST) {
                continue
            }

            val ipAddress = split[1]
            val name = split[2]

            val isNewClient = !ClientSingleton.clients.containsKey(ipAddress)
            val isConnected =
                !(ClientSingleton.clients[ipAddress]?.timeoutChecker?.isTimedOut() ?: true)

            if (isNewClient || !isConnected) {
                println("Client connecting to server...")
                var socket: Socket? = null
                var reader: BufferedReader? = null
                var writer: BufferedWriter? = null
                try {
                    socket = Socket(InetAddress.getByName(ipAddress), 4445)
                    reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                    writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                    ClientSingleton.clients[ipAddress] = Client(socket, reader, writer, ipAddress)
                    ClientSingleton.deviceList.updateOrAddDevice(
                        Device(
                            ipAddress = ipAddress,
                            user = name,
                            isPlaying = false,
                            isConnected = true
                        )
                    )
                    ClientSingleton.clients[ipAddress]?.start()
                } catch (e: Exception) {
                    println("ConnectionClient unable to create Client: $e")
                    socket?.close()
                    reader?.close()
                    writer?.close()
                }
            }
        }
    }

    fun close() {
        if (!multicastSocket.isClosed) {
            multicastSocket.close()
        }
    }
}


class Client(
    private val socket: Socket,
    private val reader: BufferedReader,
    private val writer: BufferedWriter,
    private val ipAddress: String
) : Thread() {
    val timeoutChecker = TimeoutChecker(10.seconds)

    init {
        timeoutChecker.startTimer()
    }

    private fun pingReceived() {
        timeoutChecker.startTimer()
        thread {
            sleep(3000)
            try {
                writer.write(Commands.PING)
                writer.newLine()
                writer.flush()
            } catch (_: Exception) {
            }
        }
    }

    private fun timerStateReceived(split: List<String>) {
        if(split.size != 2) {
            return
        }

        for (device in ClientSingleton.deviceList.getAsList()) {
            if (device.ipAddress == ipAddress) {
                val modifiedDevice = device.copy()
                modifiedDevice.isPlaying = false
                modifiedDevice.timerState = split[1]
                ClientSingleton.deviceList.updateDevice(modifiedDevice)
            }
        }
    }

    private fun isPlayingReceived() {
        for (device in ClientSingleton.deviceList.getAsList()) {
            if (device.ipAddress == ipAddress) {
                val modifiedDevice = device.copy()
                modifiedDevice.isPlaying = true
                modifiedDevice.timerState = null
                ClientSingleton.deviceList.updateDevice(modifiedDevice)
            }
        }
    }

    private fun isIdleReceived() {
        for (device in ClientSingleton.deviceList.getAsList()) {
            if (device.ipAddress == ipAddress) {
                val modifiedDevice = device.copy()
                modifiedDevice.isPlaying = false
                modifiedDevice.timerState = null
                ClientSingleton.deviceList.updateDevice(modifiedDevice)
            }
        }
    }

    private fun parseMessage(message: String) {
        val splitMsg = message.split(' ', limit = 2)
        when (splitMsg.first()) {
            Commands.PING -> pingReceived()
            Commands.TIMER_STATE -> timerStateReceived(splitMsg)
            Commands.IS_PLAYING -> isPlayingReceived()
            Commands.IS_IDLE -> isIdleReceived()
            else -> println("Unknown message")
        }
    }

    override fun run() {
        while (!socket.isClosed) {
            try {
                val line = reader.readLine() ?: break
                println("Client received message: $line")
                parseMessage(line)
            } catch (e: IOException) {
                println("Client unable to read line: $e")
                sleep(3000)
            }
        }
    }

    fun send(message: String) {
        println("Client to server: $message")

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
        if (!socket.isClosed) {
            socket.close()
            reader.close()
            writer.close()
        }
    }
}