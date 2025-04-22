package com.ispgr5.locationsimulator.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.net.Socket
import java.util.Objects
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.seconds

private const val TAG = "ClientHandler"

/**
 * Allows the ClientHandler to send signals to UI classes
 */
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

/**
 * The ClientHandler handles the communication between the server and client on the server side
 */
class ClientHandler(
    private val socket: Socket,
    private val reader: BufferedReader,
    private val writer: BufferedWriter,
) : Thread() {
    data class DeviceState(val isPlaying: Boolean = false, val timerState: String? = null)

    private val timeoutChecker = TimeoutChecker(10.seconds)

    /**
     * Global object for features that are not specific to a class instance
     */
    companion object {
        val clientHandlers = HashSet<ClientHandler>()
        val clientSignal = MutableLiveData<ClientSignal?>()
        val deviceState = AtomicReference(DeviceState())

        private val isCheckConnectionActive = AtomicBoolean(false)

        /**
         * Periodically check whether any of the clients is timed out
         */
        fun startCheckConnection() {
            isCheckConnectionActive.set(true)
            thread {
                while (isCheckConnectionActive.get()) {
                    sleep(3000)
                    synchronized(clientHandlers) {
                        clientHandlers.removeIfCompat{ it.timeoutChecker.isTimedOut() }
                    }
                }
            }
        }

        /**
         * Send a message to all clients
         *
         * @param message the message to send
         */
        fun sendToClients(message: String) {
            synchronized(clientHandlers) {
                for (clientHandler in clientHandlers) {
                    clientHandler.send(message)
                }
            }
        }

        /**
         * Stops checking for timed out clients
         */
        fun stopCheckConnection() {
            isCheckConnectionActive.set(false)
        }

        /**
         * Closes all client handlers
         */
        fun closeAllClientHandlers() {
            synchronized(clientHandlers) {
                for (clientHandler in clientHandlers) {
                    clientHandler.close()
                }
                clientHandlers.clear()
            }
        }
    }

    init {
        timeoutChecker.startTimer()
        synchronized(clientHandlers) { clientHandlers.add(this) }
        if(deviceState.get().isPlaying) {
            send(Commands.IS_PLAYING)
        }
        val timerState = deviceState.get().timerState
        if(timerState != null) {
            send("${Commands.TIMER_STATE} $timerState")
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
            else -> Log.w(TAG, "Unknown message: $message")
        }
    }

    override fun run() {
        send(Commands.PING)
        while (!socket.isClosed) {
            try {
                val line = reader.readLine() ?: break
                Log.i(TAG, "Server received message: $line")
                parseMessage(line)
            } catch (e: IOException) {
                Log.i(TAG, "Server unable to read line: $e")
                sleep(3000)
            }
        }
    }

    private fun send(message: String) {
        Log.i(TAG, "Server to client: $message")

        thread {
            try {
                writer.write(message)
                writer.newLine()
                writer.flush()
            } catch (e: Exception) {
                Log.w(TAG, "Server unable to send message: $e")
            }
        }
    }

    private fun close() {
        socket.close()
        reader.close()
        writer.close()
    }
}