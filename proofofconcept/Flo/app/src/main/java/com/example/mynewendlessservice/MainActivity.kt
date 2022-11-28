package com.example.mynewendlessservice

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "Just another endless vibration app"

        val startButton = findViewById<Button>(R.id.btnStart);
        val stopButton = findViewById<Button>(R.id.btnStop);

        //Intend to Start Service
        startButton.setOnClickListener {
            Intent(this, InfinityService::class.java).also {
                Log.d("debug","itAction: ${it.action}")
                it.action = "START"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(it)
                }else{
                    startService(it)
                }
            }
        }

        //Intend to Stop Service
        stopButton.setOnClickListener {
            Intent(this, InfinityService::class.java).also {
                Log.d("debug","itAction: ${it.action}")
                it.action = "STOP"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(it)
                }else{
                    startService(it)
                }
            }
        }
    }
}