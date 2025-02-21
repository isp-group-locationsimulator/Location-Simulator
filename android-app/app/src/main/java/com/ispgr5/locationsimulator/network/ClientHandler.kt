package com.ispgr5.locationsimulator.network

import androidx.lifecycle.MutableLiveData
import com.ispgr5.locationsimulator.presentation.trainerScreen.Device
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class ClientHandler(
    private val name: String,
    private val socket: Socket,
    private val reader: BufferedReader,
    private val writer: BufferedWriter,
) : Thread() {
    companion object {
        val clientHandlers: HashMap<String, ClientHandler> = HashMap()
        val deviceList = MutableLiveData<ArrayList<Device>>()

        fun isConnected(name: String): Boolean {
            val success = AtomicBoolean(true)
            thread {
                try {
                    val client = clientHandlers[name] ?: throw RuntimeException()
                    client.writer.write("Check")
                    client.writer.newLine()
                    client.writer.flush()
                } catch (e: Exception) {
                    success.set(false)
                }
            }.join()
            return success.get()
        }

        fun sendToClient(name: String, message: String) {
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
    }

    init {
        if (clientHandlers.containsKey(name)) {
            socket.close()
            reader.close()
            writer.close()
            throw RuntimeException("Name $name is already taken")
        }

        send("Success")
        clientHandlers[name] = this
        val newList = ArrayList<Device>()
        newList.addAll(deviceList.value ?: emptyList())
        newList.add(Device("User1", name, true))
        deviceList.postValue(newList)
        println(deviceList.value!!)
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
                println("ClientHandler received message: $line")
                parseMessage(line)
            } catch (e: IOException) {
                println("ClientHandler unable to read line: $e")
                sleep(3000)
            }
        }
    }

    fun send(message: String) {
        println("ClientHandler sending message: $message")

        thread {
            try {
                writer.write(message)
                writer.newLine()
                writer.flush()
            } catch (e: Exception) {
                println("ClientHandler unable to send message: $e")
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
        clientHandlers.remove(name)
        val newList = ArrayList<Device>()
        for (device in deviceList.value ?: emptyList()) {
            if (device.name != name) {
                newList.add(device)
            }
        }
        deviceList.postValue(newList)
    }
}