package com.ispgr5.locationsimulator.network

import android.net.wifi.WifiManager.MulticastLock
import androidx.lifecycle.MutableLiveData
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
import java.util.concurrent.Callable
import java.util.concurrent.FutureTask
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.seconds

sealed class ClientSignal {
    data class StartTraining(val config: String) : ClientSignal()
    data object StopTraining : ClientSignal()
}

object ClientSingleton {
    var lock: MulticastLock? = null
    val clientSignal = MutableLiveData<ClientSignal?>()
    private var connectionClient: ConnectionClient? = null
    private var client: Client? = null
    private var currentClientName: String? = null
    private val isCheckConnectionActive = AtomicBoolean(false)

    fun start(name: String): Boolean {
        if(tryConnect(name = name)) {
            currentClientName = name
            startCheckConnection()
            return true
        }
        return false
    }

    fun send(message: String) {
        client?.send(message)
    }

    fun close() {
        stopCheckConnection()
        connectionClient?.close()
        client?.close()
    }

    private fun tryConnect(name: String) : Boolean {
        if (lock == null) {
            return false
        }

        connectionClient = ConnectionClient(name)
        try {
            lock!!.acquire()

            val futureTask = FutureTask(connectionClient)
            val t = Thread(futureTask)
            t.start()

            client = futureTask.get()
            client?.start()
        } catch (e: Exception) {
            return false
        } finally {
            connectionClient?.close()
            lock!!.release()
        }
        return client != null
    }

    private fun isConnected(): Boolean {
        return client?.timeoutChecker?.isNotTimedOut() ?: false
    }

    private fun startCheckConnection() {
        isCheckConnectionActive.set(true)
        thread {
            while (isCheckConnectionActive.get() && currentClientName != null) {
                sleep(3000)
                if (!isConnected()) {
                    tryConnect(currentClientName!!)
                }
            }
        }
    }

    private fun stopCheckConnection() {
        isCheckConnectionActive.set(false)
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

            if (split.count() == 2 && split[0] == Commands.BROADCAST) {
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
        if (!multicastSocket.isClosed) {
            multicastSocket.close()
        }
    }
}


private class Client(
    name: String,
    private val socket: Socket,
    private val reader: BufferedReader,
    private val writer: BufferedWriter
) : Thread() {
    val timeoutChecker = TimeoutChecker(10.seconds)

    init {
        send(Commands.formatName(name))
        timeoutChecker.startTimer()

        val line = reader.readLine() ?: throw RuntimeException("ErrorNullAnswer")
        if (line != Commands.SUCCESS) {
            throw RuntimeException("ErrorUnknownAnswer")
        }
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

    private fun parseMessage(message: String) {
        val splitMsg = message.split(' ', limit = 2)
        when (splitMsg.first()) {
            Commands.PING -> pingReceived()
            Commands.START -> if (splitMsg.size == 2) ClientSingleton.clientSignal.postValue(
                ClientSignal.StartTraining(
                    splitMsg[1]
                )
            )
            Commands.STOP -> ClientSingleton.clientSignal.postValue(ClientSignal.StopTraining)
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