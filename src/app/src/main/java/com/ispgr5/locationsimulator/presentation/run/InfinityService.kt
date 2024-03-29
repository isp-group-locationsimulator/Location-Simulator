package com.ispgr5.locationsimulator.presentation.run

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.util.Log
import android.widget.Toast
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.ConfigurationComponentRoomConverter
import com.ispgr5.locationsimulator.presentation.MainActivity
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi

/**
 * The service that plays the configured vibrations and sounds during the simulation
 */
class InfinityService : Service() {

	private var isServiceStarted = false
	private var isConfigOrderRandom = false
	private var wakeLock: PowerManager.WakeLock? = null
	private var config: List<ConfigComponent>? = null
	private var soundPlayer: SoundPlayer = SoundPlayer {}
	private lateinit var soundsDir: String

	/**
	 * set to null, because it must be defined, since it is abstract
	 * in the superclass, but we don't need it
	 */
	override fun onBind(p0: Intent?): IBinder? = null

	/**
	 * called when the service is created
	 */
	override fun onCreate() {
		super.onCreate()
		val notification = createNotification()
		startForeground(1, notification)
	}

	/**
	 * called when the service receives an intent to start or stop the simulation
	 */
	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		if (intent != null) {
			if (intent.action == "START") {
				//set the received configuration, the sounds directory and whether the config should be played in random order
				changeConfig(intent.getStringExtra("config"))
				soundsDir = intent.getStringExtra("soundsDir").toString()
				isConfigOrderRandom = intent.getStringExtra("randomOrderPlayback").toBoolean()
				println(intent.getStringExtra("randomOrderPlayback"))
				println(intent.getStringExtra("randomOrderPlayback").toBoolean())
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
		wakeLock =
			(getSystemService(Context.POWER_SERVICE) as PowerManager).run {
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
		//duration and pause is in ms !
		when (item) {
			is ConfigComponent.Vibration -> {
				//define the duration, strength and pause duration of the vibration
				val duration =
					(Math.random() * (item.maxDuration  - item.minDuration  + 1) + item.minDuration ).toLong().coerceAtLeast(1)
				val strength =
					(Math.random() * (item.maxStrength - item.minStrength + 1) + item.minStrength).toInt().coerceAtLeast(1)
				val pause =
					(Math.random() * (item.maxPause  - item.minPause  + 1) + item.minPause ).toLong()

				//play the vibration depending on the used android version
				if (Build.VERSION.SDK_INT >= 26) {
					Log.d(
						"Signal-Infinity",
						"Creating Vibration... Duration: $duration ms , Strength: $strength"
					)
					vibrator.vibrate(
						VibrationEffect.createOneShot(
							duration,
							strength
						)
					)
				} else {
					Log.d("Signal-Infinity", "Creating Vibration... Duration: $duration ms")
					@Suppress("DEPRECATION") // Needed for the support of older Android versions.
					vibrator.vibrate(duration)
				}

				//wait for the vibration to finish and then pause for the defined time
				Log.d("Signal-Infinity", "Starting sleep... Pause: $pause ms")
				Thread.sleep(duration + pause)
			}
			is ConfigComponent.Sound -> {
				//define the duration, volume and pause duration of the sound and plays it
				val pause =
					(Math.random() * (item.maxPause  - item.minPause  + 1) + item.minPause ).toLong()
				val volume: Float =
					(item.minVolume + Math.random() * (item.maxVolume - item.minVolume)).toFloat()
				val duration =
					soundPlayer.startSound(soundsDir + item.source, volume)
				Log.d(
					"Signal-Infinity",
					"Creating Sound... Name: ${item.source} , Duration: $duration ms , Volume: $volume"
				)
				Log.d("Signal-Infinity", "Starting sleep... Pause: $pause ms")
				//wait for the sound to finish and then pause for the defined time
				Thread.sleep(duration + pause)
			}
		}
	}

	/**
	 * stops the simulation
	 */
	private fun stopService() {
		Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show()
		try {
			//release the wakeLock, since it is no longer needed
			wakeLock?.let {
				if (it.isHeld) {
					it.release()
				}
			}
			@Suppress("DEPRECATION")
			stopForeground(true) // TODO: Deprecated. Replace with stopForeground(0)?
			stopSelf()
		} catch (e: Exception) {
			Log.d("debug", "Service stopped without being started: ${e.message}")
		}
		isServiceStarted = false
	}

	/**
	 * sops the simulation if the service is destroyed
	 */
	override fun onDestroy() {
		isServiceStarted = false
	}

	/**
	 * creates the notification that is shown when the service is started
	 */
	private fun createNotification(): Notification {
		val notificationChannelId = "ENDLESS SERVICE CHANNEL"

		// depending on the Android API that we're dealing with we will have
		// to use a specific method to create the notification
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val notificationManager =
				getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			val channel = NotificationChannel(
				notificationChannelId,
				"Endless Service notifications channel",
				NotificationManager.IMPORTANCE_HIGH
			).let {
				it.description = "Endless Service channel"
				it.enableLights(true)
				it.lightColor = Color.RED
				it.enableVibration(true)
				it.vibrationPattern = longArrayOf(0)
				it
			}
			notificationManager.createNotificationChannel(channel)
		}

		// TODO: PendingIntent.FLAG_MUTABLE requires API-Level 31? Should be checked and updated if necessary
		val pendingIntent: PendingIntent =
			Intent(this, MainActivity::class.java).let { notificationIntent ->
				PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)
			}

		@Suppress("DEPRECATION") // Needed for the support of older Android versions.
		val builder: Notification.Builder =
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
				this,
				notificationChannelId
			) else Notification.Builder(this)

		@Suppress("DEPRECATION")
		return builder
			.setContentTitle("Endless Vibration")
			.setContentText("I hope this will vibrate forever!")
			.setContentIntent(pendingIntent)
			.setSmallIcon(R.mipmap.ic_launcher2)
			.setTicker("Ticker text")
			.setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
			.build()
	}
}