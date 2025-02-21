package com.ispgr5.locationsimulator.network

import android.net.wifi.WifiManager.MulticastLock
import androidx.lifecycle.MutableLiveData
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.MulticastSocket
import java.net.Socket
import java.util.concurrent.Callable
import java.util.concurrent.FutureTask
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread


object ClientSingleton {
    private var client: Client? = null
    var lock: MulticastLock? = null
    val simulationSettings = MutableLiveData<String>()

    fun tryConnect(name: String): Boolean {
        if (lock == null) {
            return false
        }

        lock!!.acquire()
        val connectionClient = ConnectionClient(name)

        val futureTask = FutureTask(connectionClient)
        val t = Thread(futureTask)
        t.start()

        client = futureTask.get()
        connectionClient.close()
        lock!!.release()
        client?.start()
        return client != null
    }

    fun send(message: String) {
        client?.send(message)
    }

    fun isConnected(): Boolean {
        return client?.checkConnection() ?: false
    }

    fun close() {
        client?.close()
    }
}

private class ConnectionClient(private val name: String) : Callable<Client> {
    private val multicastSocket: MulticastSocket = MulticastSocket(4445)
    private val inetSocketAddress = InetSocketAddress("230.0.0.0", 4445)
    private var buf: ByteArray = ByteArray(256)

    override fun call(): Client? {
        multicastSocket.joinGroup(inetSocketAddress, null)
        val packet = DatagramPacket(buf, buf.size)

        while (true) {
            try {
                multicastSocket.receive(packet)
            } catch (e: IOException) {
                println("Client unable to receive multicast packet: $e")
                return null
            }

            val received = String(
                packet.data, 0, packet.length
            )
            val split = received.split(' ')

            if (split.count() == 2 && split[0] == "TestBroadcast") {
                println("Client connecting to server...")
                var socket: Socket? = null
                var reader: BufferedReader? = null
                var writer: BufferedWriter? = null
                try {
                    socket = Socket(InetAddress.getByName(split[1]), 4445)
                    reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                    writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                    return Client(name, socket, reader, writer)
                } catch (e: Exception) {
                    println("ConnectionClient unable to create Client: $e")
                    socket?.close()
                    reader?.close()
                    writer?.close()
                    return null
                }
            }
        }
    }

    fun close() {
        multicastSocket.leaveGroup(inetSocketAddress, null)
        multicastSocket.close()
    }
}


private class Client(
    name: String,
    private val socket: Socket,
    private val reader: BufferedReader,
    private val writer: BufferedWriter
) : Thread() {
    init {
        send("Name $name")

        val line = reader.readLine() ?: throw RuntimeException("ErrorNullAnswer")
        if (line != "Success") {
            throw RuntimeException("ErrorUnknownAnswer")
        }
    }

    private fun parseMessage(message: String) {
        when (message) {

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
        println("Client sending message: $message")

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

    fun checkConnection(): Boolean {
        val success = AtomicBoolean(true)
        thread {
            try {
                writer.write("Check")
                writer.newLine()
                writer.flush()
            } catch (e: Exception) {
                success.set(false)
            }
        }.join()
        return success.get()
    }

    fun close() {
        socket.close()
        reader.close()
        writer.close()
    }
}