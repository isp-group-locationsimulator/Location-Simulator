package com.ispgr5.locationsimulator.network

import android.util.Log
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "Server"

/**
 * The ServerSingleton is used for easy global interaction with server related classes.
 * It is used for Remote devices
 */
object ServerSingleton {
    // global variables that need to be modified for various features to work
    var remoteName: String? = null
    var keepScreenOn: () -> Unit = {}
    var doNotKeepScreenOn: () -> Unit = {}

    private var multicastServer: MulticastServer? = null
    private var server: Server? = null
    private var isActive = false

    /**
     * Starts the server.
     * Multicasts will be send to allow clients to find the server and connections from clients will
     * be accepted
     */
    fun start(name: String) {
        if (isActive) return

        multicastServer = MulticastServer(name)
        server = Server()

        multicastServer!!.start()
        server!!.start()
        ClientHandler.startCheckConnection()
        keepScreenOn()
        isActive = true
    }

    /**
     * Closes the server.
     * Multicasts will no longer be send and clients will not be able to connect
     */
    fun close() {
        if (!isActive) return

        multicastServer!!.stopMulticast()
        multicastServer!!.close()
        ClientHandler.stopCheckConnection()
        ClientHandler.closeAllClientHandlers()
        server!!.close()
        doNotKeepScreenOn()
        isActive = false
    }
}

/**
 * The MulticastServer multicasts messages to allow clients to find the server
 */
private class MulticastServer(val userName: String) : Thread() {
    private var ipAddress = getIPAddress().toString()
    private val broadcastSocket = DatagramSocket()
    private val isActive = AtomicBoolean(true)

    override fun run() {
        while (isActive.get()) {
            ipAddress = if (ipAddress == "null") getIPAddress().toString() else ipAddress

            try {
                sleep(2000)
                val buf = Commands.formatBroadcast(ipAddress, userName).toByteArray()
                val group = InetAddress.getByName("230.0.0.0")

                val packet = DatagramPacket(buf, buf.size, group, 4445)
                broadcastSocket.send(packet)
            } catch (e: InterruptedException) {
                Log.w(TAG, "Server unable to wait between sending packets: $e")
                return
            } catch (e: Exception) {
                Log.w(TAG, "Server unable to send multicast packet: $e")
            }

        }
    }

    fun stopMulticast() {
        isActive.set(false)
    }

    fun close() {
        broadcastSocket.close()
    }
}

/**
 * The Server accepts new client connections and creates a new ClientHandler for each client that
 * connects
 */
private class Server : Thread() {
    private val serverSocket: ServerSocket = ServerSocket(4445)

    override fun run() {
        while (!serverSocket.isClosed) {
            var socket: Socket? = null
            var reader: BufferedReader? = null
            var writer: BufferedWriter? = null
            try {
                socket = serverSocket.accept()
                Log.i(TAG, "Server connecting to client...")
                reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                ClientHandler(socket, reader, writer).start()
            } catch (e: Exception) {
                Log.w(TAG, "Server unable to accept client: $e")
                socket?.close()
                reader?.close()
                writer?.close()
            }
        }
    }

    fun close() {
        serverSocket.close()
    }
}