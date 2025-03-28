package com.ispgr5.locationsimulator.network

import androidx.lifecycle.MutableLiveData
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.net.Socket
import java.util.Objects
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.seconds

sealed class ClientSignal {
    data class StartTraining(val config: String, val hours: Long, val minutes: Long, val seconds: Long) : ClientSignal()
    data object StopTraining : ClientSignal()
}

// only exists because removeIf requires API level 24 and the app uses API level 21
fun<E> MutableCollection<E>.removeIfCompat(filter: (E) -> Boolean) {
    Objects.requireNonNull(filter)
    val each: MutableIterator<E> = this.iterator()
    while (each.hasNext()) {
        if (filter.invoke(each.next())) {
            each.remove()
        }
    }
}

class ClientHandler(
    private val socket: Socket,
    private val reader: BufferedReader,
    private val writer: BufferedWriter,
) : Thread() {
    private val timeoutChecker = TimeoutChecker(10.seconds)

    companion object {
        val clientHandlers = HashSet<ClientHandler>()
        val clientSignal = MutableLiveData<ClientSignal?>()

        private val isCheckConnectionActive = AtomicBoolean(false)

        fun startCheckConnection() {
            isCheckConnectionActive.set(true)
            thread {
                while (isCheckConnectionActive.get()) {
                    sleep(3000)
                    clientHandlers.removeIfCompat{ it.timeoutChecker.isTimedOut() }
                }
            }
        }

        fun sendToClients(message: String) {
            for (clientHandler in clientHandlers) {
                clientHandler.send(message)
            }
        }

        fun stopCheckConnection() {
            isCheckConnectionActive.set(false)
        }

        fun closeAllClientHandlers() {
            for (clientHandler in clientHandlers) {
                clientHandler.close()
            }
        }
    }

    init {
        timeoutChecker.startTimer()
        clientHandlers.add(this)
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

    private fun startReceived(split: List<String>) {
        if(split.size != 3) {
            return
        }

        val config = split[2]
        var hours = 0L
        var minutes = 0L
        var seconds = 0L

        val splitTime = split[1].split(':', limit = 3)
        if (splitTime.size == 3) {
            hours = splitTime[0].toLongOrNull() ?: 0L
            minutes = splitTime[1].toLongOrNull() ?: 0L
            seconds = splitTime[2].toLongOrNull() ?: 0L
        }

        clientSignal.postValue(
            ClientSignal.StartTraining(config, hours, minutes, seconds)
        )
    }

    private fun stopReceived() {
        clientSignal.postValue(ClientSignal.StopTraining)
    }

    private fun parseMessage(message: String) {
        val splitMsg = message.split(' ', limit = 3)
        when (splitMsg.first()) {
            Commands.PING -> pingReceived()
            Commands.START -> startReceived(splitMsg)
            Commands.STOP -> stopReceived()
            else -> println("Unknown message")
        }
    }

    override fun run() {
        send(Commands.PING)
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

    private fun send(message: String) {
        println("Server to client: $message")

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
        clientHandlers.remove(this)
    }
}