package com.ispgr5.locationsimulator.network

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.util.concurrent.atomic.AtomicBoolean

object ServerSingleton {
    private val multicastServer = MulticastServer()
    private val server = Server()

    fun start() {
        multicastServer.start()
        server.start()
        ClientHandler.startCheckConnection()
    }

    fun close() {
        multicastServer.stopMulticast()
        multicastServer.close()
        ClientHandler.stopCheckConnection()
        ClientHandler.closeAllClientHandlers()
        server.close()
    }
}

private class MulticastServer : Thread() {
    private val ipAddress = getIPAddress().toString()
    private val broadcastSocket = DatagramSocket()
    private val isActive = AtomicBoolean(true)

    override fun run() {
        while (isActive.get()) {
            try {
                sleep(2000)
                val buf = "TestBroadcast ".toByteArray() + ipAddress.toByteArray()
                val group = InetAddress.getByName("230.0.0.0")

                val packet = DatagramPacket(buf, buf.size, group, 4445)
                broadcastSocket.send(packet)
            } catch (e: InterruptedException) {
                println("Server unable to wait between sending packets: $e")
                return
            } catch (e: Exception) {
                println("Server unable to send multicast packet: $e")
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

class Server : Thread() {
    private val serverSocket: ServerSocket = ServerSocket(4445)

    private fun createClientHandler(
        socket: Socket,
        reader: BufferedReader,
        writer: BufferedWriter
    ): Boolean {
        try {
            val line = reader.readLine() ?: return false
            val split = line.split(' ')

            if (split.count() == 2 && split[0] == "Name") {
                val handler = ClientHandler(split[1], socket, reader, writer)
                handler.start()
            }
        } catch (e: IOException) {
            println("Server unable to read line: $e")
            return false
        } catch (e: RuntimeException) {
            println("Error while creating ClientHandler: $e")
            return false
        }

        return true
    }

    override fun run() {
        while (!serverSocket.isClosed) {
            try {
                val socket = serverSocket.accept()
                println("Server connecting to client...")
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                if (!createClientHandler(socket, reader, writer)) {
                    println("Server unable to connect to client")
                    socket.close()
                    reader.close()
                    writer.close()
                }
            } catch (e: SocketException) {
                println("Server unable to accept client: $e")
                break
            }
        }
    }

    fun close() {
        serverSocket.close()
    }
}