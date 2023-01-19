package com.ispgr5.locationsimulator.presentation.run

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.util.Log
import android.widget.Toast
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.ConfigurationComponentConverter
import com.ispgr5.locationsimulator.domain.model.Vibration
import com.ispgr5.locationsimulator.presentation.MainActivity
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi


class InfinityService:Service() {


    private var isServiceStarted = false
    private var wakeLock: PowerManager.WakeLock? = null
    private var config: List<ConfigComponent>? = null

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val notification = createNotification()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent != null){
            if(intent.action == "START"){
                changeConfig(intent.getStringExtra("config"))
                if(config !=null){
                    startService()
                }
            }else if(intent.action == "STOP"){
                stopService()
            }
        }
        return START_STICKY
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun changeConfig(config: String?) {
        if(config != null){
            this.config = ConfigurationComponentConverter().componentStrToComponentList(config)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startService() {
        if (isServiceStarted) return
        Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show()
        isServiceStarted = true

        // we need this lock so our service gets not affected by Doze Mode
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                    acquire()
                }
            }

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        GlobalScope.launch(Dispatchers.Default) {
            while (isServiceStarted) {
                for(item in config!!){
                    if(item is Vibration){
                        val duration = (Math.random() * (item.maxDuration*1000 - item.minDuration*1000 + 1) + item.minDuration*1000).toLong()
                        val strength = (Math.random() * (item.maxStrength - item.minStrength + 1) + item.minStrength).toInt()
                        val pause = (Math.random() * (item.maxPause*1000 - item.minPause*1000 + 1) + item.minPause*1000).toLong()
                        if (Build.VERSION.SDK_INT >= 26) {
                            Log.d("vib666", "Creating Vibration... Duration:$duration , Strength:$strength")
                            vibrator.vibrate(VibrationEffect.createOneShot(duration,strength))
                        } else {
                            Log.d("vib666","Creating Vibration... Duration:$duration")
                            vibrator.vibrate(duration)
                        }
                        Log.d("vib666","Starting sleep...Pause:$pause")
                        Thread.sleep(duration+pause)
                    }
                    if(!isServiceStarted){
                        break
                    }
                }
            }
        }
        // TODO: Could possibly be overworked
        GlobalScope.launch(Dispatchers.Default) {
            var stop = false
            while (!stop){
                if(!isServiceStarted){
                    vibrator.cancel()
                    stop = true
                }
            }
        }
    }

    private fun stopService() {
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show()
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            Log.d("debug","Service stopped without being started: ${e.message}")
        }
        isServiceStarted = false
    }

    override fun onDestroy(){
        isServiceStarted = false
    }


    private fun createNotification(): Notification {
        val notificationChannelId = "ENDLESS SERVICE CHANNEL"

        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)
        }

        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
            this,
            notificationChannelId
        ) else Notification.Builder(this)

        return builder
            .setContentTitle("Endless Vibration")
            .setContentText("I hope this will vibrate forever!")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker("Ticker text")
            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
            .build()
    }
}