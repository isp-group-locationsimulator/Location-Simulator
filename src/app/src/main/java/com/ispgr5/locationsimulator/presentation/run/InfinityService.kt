package com.ispgr5.locationsimulator.presentation.run

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.ConfigurationComponentRoomConverter
import com.ispgr5.locationsimulator.presentation.MainActivity
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi

private const val TAG = "InfinityService"

object ServiceIntentKeys {
    const val CONFIG_JSON_STRING = "configJson"
    const val PATTERN_NAME_STRING = "patternName"
    const val RANODM_ORDER_PLAYBACK_BOOLEAN = "randomOrderPlayback"
    const val SOUNDS_DIR_STRING = "soundsDir"
}

/**
 * The service that plays the configured vibrations and sounds during the simulation
 */
class InfinityService : LifecycleService() {

    companion object {
        val EffectEventBus = MutableLiveData<EffectParameters?>()
        // TODO: next effect (including when), next pause, state all on the bus?
    }

    private var isServiceStarted = false
    private var isConfigOrderRandom = false
    private var wakeLock: PowerManager.WakeLock? = null
    private var config: List<ConfigComponent>? = null
    private var soundPlayer: SoundPlayer = SoundPlayer {}
    private var shownNotification: Notification? = null
    private lateinit var soundsDir: String


    /**
     * called when the service is created
     */
    override fun onCreate() {
        super.onCreate()
        shownNotification = createNotification("TODO") // TODO
        startForeground(1, shownNotification)
    }

    /**
     * called when the service receives an intent to start or stop the simulation
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent != null) {
            if (intent.action == "START") {
                //set the received configuration, the sounds directory and whether the config should be played in random order
                val configJson = intent.getStringExtra(ServiceIntentKeys.CONFIG_JSON_STRING)
                changeConfig(configJson)
                shownNotification = createNotification(intent.getStringExtra(ServiceIntentKeys.PATTERN_NAME_STRING)!!)
                startForeground(1, shownNotification)
                soundsDir = intent.getStringExtra(ServiceIntentKeys.SOUNDS_DIR_STRING).toString()
                isConfigOrderRandom = intent.getStringExtra(ServiceIntentKeys.RANODM_ORDER_PLAYBACK_BOOLEAN).toBoolean()
                //start the service if the config is defined
                if (config != null) {
                    startService()
                }
            } else if (intent.action == "STOP") {
                stopService()
            }
        }
        return START_STICKY
    }

    /**
     * changes the current configuration
     */
    @OptIn(ExperimentalSerializationApi::class)
    private fun changeConfig(config: String?) {
        if (config != null) {
            this.config = ConfigurationComponentRoomConverter().componentStrToComponentList(config)
        }
    }

    /**
     * starts the simulation
     */
    @SuppressLint("WakelockTimeout")
    @OptIn(DelicateCoroutinesApi::class)
    private fun startService() {
        //make sure the service starts only once
        if (isServiceStarted) return
        Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show()
        isServiceStarted = true

        // we need this lock so our service gets not affected by Doze Mode
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                acquire() // Should we set a timeout for this wakelock?
            }
        }

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION") // Needed for the support of older Android versions.
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        GlobalScope.launch(Dispatchers.Default) {
            while (isServiceStarted) {
                if (isConfigOrderRandom) {
                    //if the config should be played in random order play a random sound or vibration
                    playSoundOrVibration(config!!.random(), vibrator)
                } else {
                    //else play the config in the defined order
                    for (item in config!!) {
                        playSoundOrVibration(item, vibrator)
                        if (!isServiceStarted) {
                            break
                        }
                    }
                }
            }
        }
        // TODO: Could possibly be overworked
        GlobalScope.launch(Dispatchers.Default) {
            //this coroutine stops the simulation when the stop button was pressed
            var stop = false
            while (!stop) {
                if (!isServiceStarted) {
                    vibrator.cancel()
                    soundPlayer.stopPlayback()
                    stop = true
                }
            }
        }
    }

    /**
     * This function plays a Sound or a Vibration, depending on the given item.
     * For the Vibration the Vibrator is used.
     * @param item The Sound or Vibration that should be played.
     * @param vibrator The Vibrator that should be used to play the Vibrations.
     */
    private fun playSoundOrVibration(item: ConfigComponent, vibrator: Vibrator) {
        val parameters = when (item) {
            is ConfigComponent.Vibration -> determineVibrationEffect(item)
            is ConfigComponent.Sound -> determineSoundEffect(item)
        }
        statusCallback(parameters)
        val playedDuration = playEffect(parameters, vibrator)
        pauseFor(parameters, playedDuration)
    }

    private fun pauseFor(parameters: EffectParameters, playedDuration: Long) {
        //wait for the vibration to finish and then pause for the defined time
        Log.d(TAG, "Starting sleep... Pause: ${parameters.pauseMillis} ms")
        Thread.sleep(playedDuration + parameters.pauseMillis)
    }

    private fun playEffect(parameters: EffectParameters, vibrator: Vibrator): Long {
        return when (parameters) {
            is EffectParameters.VibrationParameters -> playVibrationEffect(parameters, vibrator)
            is EffectParameters.SoundParameters -> playSoundEffect(parameters)
        }
    }

    private fun playSoundEffect(parameters: EffectParameters.SoundParameters): Long {
        val duration =
            soundPlayer.startSound(parameters.soundUriAsString, parameters.volume).toLong()
        Log.d(
            TAG,
            "Creating Sound... Name: ${parameters.soundUriAsString} , Duration: $duration ms , Volume: ${parameters.volume}"
        )
        return duration
    }

    private fun statusCallback(parameters: EffectParameters?) {
        EffectEventBus.postValue(parameters)
        Log.i(TAG, "statusCallback: pushed $parameters to the bus")
    }

    private fun determineSoundEffect(item: ConfigComponent.Sound): EffectParameters.SoundParameters {
        //define the duration, volume and pause duration of the sound and plays it
        val pause =
            (Math.random() * (item.maxPause - item.minPause + 1) + item.minPause).toLong()
        val volume: Float =
            (item.minVolume + Math.random() * (item.maxVolume - item.minVolume)).toFloat()
        return EffectParameters.SoundParameters(
            pauseMillis = pause,
            volume = volume,
            soundUriAsString = soundsDir + item.source
        )
    }

    private fun determineVibrationEffect(item: ConfigComponent.Vibration): EffectParameters {
        //define the duration, strength and pause duration of the vibration
        val duration =
            (Math.random() * (item.maxDuration - item.minDuration + 1) + item.minDuration).toLong()
                .coerceAtLeast(1)
        val strength =
            (Math.random() * (item.maxStrength - item.minStrength + 1) + item.minStrength).toInt()
                .coerceAtLeast(1)
        val pause =
            (Math.random() * (item.maxPause - item.minPause + 1) + item.minPause).toLong()

        return EffectParameters.VibrationParameters(
            durationMillis = duration,
            pauseMillis = pause,
            strength = strength
        )
    }

    private fun playVibrationEffect(
        effect: EffectParameters.VibrationParameters,
        vibrator: Vibrator
    ): Long {
        //play the vibration depending on the used android version
        if (Build.VERSION.SDK_INT >= 26) {
            Log.d(
                TAG,
                "Creating Vibration... Duration: ${effect.durationMillis} ms , Strength: ${effect.strength}"
            )
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    effect.durationMillis,
                    effect.strength
                )
            )
        } else {
            Log.d(TAG, "Creating Vibration... Duration: ${effect.durationMillis} ms")
            @Suppress("DEPRECATION") // Needed for the support of older Android versions.
            vibrator.vibrate(effect.durationMillis)
        }
        return effect.durationMillis
    }

    /**
     * stops the simulation
     */
    private fun stopService() {
        EffectEventBus.postValue(null)
        Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show()
        try {
            //release the wakeLock, since it is no longer needed
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                @Suppress("DEPRECATION")
                stopForeground(true)
            }
            stopSelf()
        } catch (e: Exception) {
            Log.e(TAG, "Service stopped without being started: ${e.message}", e)
        }
        isServiceStarted = false
    }

    /**
     * sops the simulation if the service is destroyed
     */
    override fun onDestroy() {
        super.onDestroy()
        isServiceStarted = false
    }

    /**
     * creates the notification that is shown when the service is started
     */
    private fun createNotification(configurationName: String): Notification {
        val notificationChannelId = "LocationSimulator Service"

        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                notificationChannelId,
                applicationContext.getString(R.string.service_notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description =
                    applicationContext.getString(R.string.service_notification_channel_description)
                enableVibration(false)
                vibrationPattern = longArrayOf(0)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // TODO: PendingIntent.FLAG_MUTABLE requires API-Level 31? Should be checked and updated if necessary
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            }

        @Suppress("DEPRECATION") // Needed for the support of older Android versions.
        val builder: Notification.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
                this,
                notificationChannelId
            ) else Notification.Builder(this)

        @Suppress("DEPRECATION")
        return builder
            .setContentTitle(applicationContext.getString(R.string.service_notification_title))
            .setContentText(
                applicationContext.getString(
                    R.string.service_notification_contentText,
                    configurationName
                )
            )
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher2)
            .setTicker("Ticker text")
            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
            .build()
    }
}

sealed class EffectParameters(val pauseMillis: Long) {
    class VibrationParameters(
        val durationMillis: Long,
        pauseMillis: Long,
        val strength: Int
    ) : EffectParameters(pauseMillis) {
        override fun toString(): String {
            return "VibrationParameters(durationMillis=$durationMillis, strength=$strength, pauseMillis=$pauseMillis)"
        }
    }

    class SoundParameters(
        pauseMillis: Long,
        val volume: Float,
        val soundUriAsString: String
    ) : EffectParameters(pauseMillis) {
        override fun toString(): String {
            return "SoundParameters(volume=$volume, soundUriAsString='$soundUriAsString', pauseMillis=$pauseMillis)"
        }
    }
}