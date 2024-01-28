package com.ispgr5.locationsimulator.presentation.run

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.*
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.ConfigurationComponentRoomConverter
import com.ispgr5.locationsimulator.presentation.MainActivity
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import org.joda.time.Instant
import java.io.File
import java.math.BigDecimal
import java.util.Timer
import kotlin.concurrent.schedule
import kotlin.random.Random

private const val TAG = "SimulationService"

object ServiceIntentKeys {
    const val CONFIG_JSON_STRING = "configJson"
    const val PATTERN_NAME_STRING = "patternName"
    const val RANDOM_ORDER_PLAYBACK_BOOLEAN = "randomOrderPlayback"
    const val SOUNDS_DIR_STRING = "soundsDir"
}

/**
 * The service that plays the configured vibrations and sounds during the simulation
 */
class SimulationService : LifecycleService() {

    companion object {
        val EffectTimelineStateBus = MutableLiveData<EffectTimelineState?>()
        val IsPlayingEventBus = MutableLiveData(false)
    }

    private val effectTimer = Timer()
    private val pauseTimer = Timer()

    //private var isServiceStarted = false
    private var isConfigOrderRandom = false
    private var wakeLock: PowerManager.WakeLock? = null
    private var config: List<ConfigComponent>? = null
    private var lastIndex: Int? = null
    private var soundPlayer: SoundPlayer = SoundPlayer {}
    private var shownNotification: Notification? = null
    private lateinit var soundsDir: String

    private val vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION") // Needed for the support of older Android versions.
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
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
                shownNotification =
                    createNotification(intent.getStringExtra(ServiceIntentKeys.PATTERN_NAME_STRING)!!)
                startForeground(1, shownNotification)
                soundsDir = intent.getStringExtra(ServiceIntentKeys.SOUNDS_DIR_STRING).toString()
                isConfigOrderRandom =
                    intent.getStringExtra(ServiceIntentKeys.RANDOM_ORDER_PLAYBACK_BOOLEAN)
                        .toBoolean()
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
    private fun startService() {
        if (IsPlayingEventBus.value == true) {
            //make *really* sure the service starts only once
            return
        }
        IsPlayingEventBus.postValue(true)

        // we need this lock so our service gets not affected by Doze Mode
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).let {
            it.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "${SimulationService::class.simpleName}::lock"
            ).apply {
                acquire() // Should we set a timeout for this wakelock?
            }
        }
        startPlayback()
    }

    private fun startPlayback() {
        val startedAt = Instant.now()
        val firstPauseDuration = 500L
        val firstEventOffset = startedAt.plus(firstPauseDuration)
        // we offset the first event, so we can set up the service in this half-second we give ourselves
        val firstEffect = determineAnEffect(firstEventOffset)
        val initialTimeline = EffectTimelineState(
            snapshotDate = startedAt,
            playingEffect = null,
            nextEffect = firstEffect,
            startPauseAt = startedAt,
            currentPauseDuration = firstPauseDuration,
            pauseReferenceRange = BigDecimal.valueOf(firstPauseDuration) to BigDecimal.valueOf(
                firstPauseDuration
            )
        )
        activateTimelineSnapshot(initialTimeline)
    }

    private fun activateTimelineSnapshot(timeline: EffectTimelineState) {
        EffectTimelineStateBus.postValue(timeline)
        schedule(timeline.nextEffect)
    }

    private fun schedule(effectToSchedule: EffectParameters) {
        Log.d(TAG, "schedule: $effectToSchedule")
        effectTimer.schedule(effectToSchedule.startAt.toDate()) {
            if (IsPlayingEventBus.value == true) {
                EffectTimelineStateBus.postValue(
                    EffectTimelineState(
                        snapshotDate = Instant.now(),
                        playingEffect = effectToSchedule,
                        nextEffect = determineAnEffect(effectToSchedule.endPauseAt),
                        startPauseAt = null,
                        currentPauseDuration = null,
                        pauseReferenceRange = null
                    )
                )
                when (effectToSchedule) {
                    is EffectParameters.Sound -> playSoundEffect(effectToSchedule)
                    is EffectParameters.Vibration -> playVibrationEffect(effectToSchedule)
                }
            }
        }
        pauseTimer.schedule(effectToSchedule.endEffectAt.toDate()) {
            if (IsPlayingEventBus.value == true) {
                Log.d(
                    TAG,
                    "schedule: pausing until ${effectToSchedule.endPauseAt} for ${effectToSchedule.pauseMillis}ms"
                )
                updateTimelineDuringPause(effectToSchedule.pauseMillis, effectToSchedule)
            }
        }
    }

    private fun updateTimelineDuringPause(
        pauseDuration: Long,
        effectToSchedule: EffectParameters
    ) {
        val previousTimeline = EffectTimelineStateBus.value ?: run {
            stopSelf()
            return
        }
        val now = Instant.now()
        val nextEffect = previousTimeline.nextEffect
        val pauseBounds = when (effectToSchedule.original) {
            is ConfigComponent.Vibration -> effectToSchedule.original.minPause to effectToSchedule.original.maxPause
            is ConfigComponent.Sound -> effectToSchedule.original.minPause to effectToSchedule.original.maxPause
        }
        activateTimelineSnapshot(
            timeline = EffectTimelineState(
                snapshotDate = now,
                playingEffect = null,
                nextEffect = nextEffect,
                startPauseAt = now,
                currentPauseDuration = pauseDuration,
                pauseReferenceRange = BigDecimal.valueOf(pauseBounds.first.toLong()) to BigDecimal.valueOf(
                    pauseBounds.second.toLong()
                )
            )
        )
    }

    private fun determineAnEffect(startAtInstant: Instant): EffectParameters {
        val nextIndex: Int = when (isConfigOrderRandom) {
            true -> {
                val indexSequence = generateSequence {
                    Random.nextInt(0, config!!.size)
                }
                indexSequence.first {
                    it != lastIndex
                    // also works for the case where nextIndex is null,
                    // since any number is non-equal to null in Kotlin
                }
            }

            false -> when (lastIndex) {
                null -> 0
                else -> {
                    val nextIndex = lastIndex!! + 1
                    nextIndex.mod(config!!.size)
                }
            }
        }
        val nextConfig = config!![nextIndex]
        lastIndex = nextIndex

        return determineEffectFromConfigComponent(
            nextConfig = nextConfig,
            startAtInstant = startAtInstant
        )
    }

    private fun determineEffectFromConfigComponent(
        nextConfig: ConfigComponent,
        startAtInstant: Instant
    ): EffectParameters = when (nextConfig) {
        is ConfigComponent.Vibration -> determineVibrationEffect(startAtInstant, nextConfig)
        is ConfigComponent.Sound -> determineSoundEffect(startAtInstant, nextConfig)
    }

    private fun playSoundEffect(parameters: EffectParameters.Sound): Long {
        val soundUriAsString = soundsDir + parameters.soundName
        val duration = soundPlayer.startSound(soundUriAsString, parameters.volume).toLong()
        Log.d(
            TAG,
            "Creating Sound... Name: $soundUriAsString, Duration: $duration ms, Volume: ${parameters.volume}"
        )
        return duration
    }

    private fun determineSoundEffect(
        startAtInstant: Instant,
        item: ConfigComponent.Sound
    ): EffectParameters.Sound {
        //define the duration, volume and pause duration of the sound
        val pause =
            (Math.random() * (item.maxPause - item.minPause + 1) + item.minPause).toLong()
        val volume: Float =
            (item.minVolume + Math.random() * (item.maxVolume - item.minVolume)).toFloat()
        return EffectParameters.Sound(
            startAt = startAtInstant,
            durationMillis = MediaDurationDeterminer.getMediaFileDuration(soundsDir + item.source),
            pauseMillis = pause,
            volume = volume,
            soundName = item.source,
            original = item
        )
    }

    private fun determineVibrationEffect(
        startAtInstant: Instant,
        item: ConfigComponent.Vibration
    ): EffectParameters.Vibration {
        //define the duration, strength and pause duration of the vibration
        val duration =
            (Math.random() * (item.maxDuration - item.minDuration + 1) + item.minDuration).toLong()
                .coerceAtLeast(1)
        val strength =
            (Math.random() * (item.maxStrength - item.minStrength + 1) + item.minStrength).toInt()
                .coerceAtLeast(1)
        val pause =
            (Math.random() * (item.maxPause - item.minPause + 1) + item.minPause).toLong()

        return EffectParameters.Vibration(
            startAt = startAtInstant,
            durationMillis = duration,
            pauseMillis = pause,
            strength = strength,
            original = item
        )
    }

    private fun playVibrationEffect(effect: EffectParameters.Vibration): Long? {
        if (IsPlayingEventBus.value != true) {
            return null
        }
        //play the vibration depending on the used android version
        if (Build.VERSION.SDK_INT >= 26) {
            Log.d(
                TAG,
                "Creating Vibration... Duration: ${effect.durationMillis} ms, Strength: ${effect.strength}"
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
        EffectTimelineStateBus.postValue(null)
        IsPlayingEventBus.postValue(false)
        soundPlayer.stopPlayback()
        vibrator.cancel()
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
    }

    /**
     * sops the simulation if the service is destroyed
     */
    override fun onDestroy() {
        super.onDestroy()
        IsPlayingEventBus.postValue(false)
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
            .setTicker("Ticker text") //TODO
            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
            .build()
    }
}

sealed class EffectParameters(
    val startAt: Instant,
    val durationMillis: Long,
    val pauseMillis: Long,
    val original: ConfigComponent
) {
    val endEffectAt: Instant get() = startAt.plus(durationMillis)
    val endPauseAt: Instant get() = endEffectAt.plus(pauseMillis)

    class Vibration(
        startAt: Instant,
        durationMillis: Long,
        pauseMillis: Long,
        val strength: Int,
        original: ConfigComponent.Vibration
    ) : EffectParameters(startAt, durationMillis, pauseMillis, original) {
        override fun toString(): String {
            return "VibrationParameters(startAt=$startAt, durationMillis=$durationMillis, strength=$strength, endEffectAt=$endEffectAt, pauseMillis=$pauseMillis, endPauseAt=${endPauseAt})"
        }
    }

    class Sound(
        startAt: Instant,
        durationMillis: Long,
        pauseMillis: Long,
        val volume: Float,
        val soundName: String,
        original: ConfigComponent.Sound
    ) : EffectParameters(startAt, durationMillis, pauseMillis, original) {
        override fun toString(): String {
            return "SoundParameters(startAt=$startAt, duration=$durationMillis, volume=$volume, soundName='$soundName', endEffectAt=$endEffectAt, pauseMillis=$pauseMillis, endPauseAt=${endPauseAt})"
        }
    }
}

data class EffectTimelineState(
    val snapshotDate: Instant,
    val playingEffect: EffectParameters?,
    val nextEffect: EffectParameters,
    val startPauseAt: Instant?,
    val currentPauseDuration: Long?,
    val pauseReferenceRange: Pair<BigDecimal, BigDecimal>?
)

object MediaDurationDeterminer {
    private val durationCache = mutableMapOf<String, Long>()
    fun getMediaFileDuration(soundLocation: String): Long {
        return when (val storedDuration = durationCache[soundLocation]) {
            null -> {
                val mediaMetadataRetriever = MediaMetadataRetriever().apply {
                    this.setDataSource(File(soundLocation).absolutePath)
                }
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!
                    .toLong().also {
                        durationCache[soundLocation] = it
                    }
            }

            else -> storedDuration
        }

    }
}