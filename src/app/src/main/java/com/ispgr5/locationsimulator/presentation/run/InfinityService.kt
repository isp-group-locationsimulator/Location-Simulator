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
import com.ispgr5.locationsimulator.presentation.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class InfinityService:Service() {

    val TAG = "debug"

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
                startService()
            }else if(intent.action == "STOP"){
                stopService()
            }
        }
        return START_STICKY
    }

    private fun changeConfig(config: String?) {
        Log.d("config111", config!!)
    }

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

        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(1000, 500)
        // we're starting a loop in a coroutine
        GlobalScope.launch(Dispatchers.Default) {
            while (isServiceStarted) {
                launch(Dispatchers.IO) {
                    if (Build.VERSION.SDK_INT >= 26) {
                        vibrator.vibrate(VibrationEffect.createWaveform(pattern, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        vibrator.vibrate(pattern,0)
                    }
                }
                delay(1500)
            }
        }
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




    private fun vibrate() {
        val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if(isServiceStarted){
            val pattern = longArrayOf(1000, 500)
            v.vibrate(pattern, 0)
        }else{
            v.cancel()
        }
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
                it.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
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