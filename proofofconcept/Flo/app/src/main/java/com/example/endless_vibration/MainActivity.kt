package com.example.endless_vibration

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "Endless Vibration"

        var startButton = findViewById<Button>(R.id.start);
        startButton.setOnClickListener {
            actionOnService(Actions.START)
        }

        var stopButton = findViewById<Button>(R.id.stop);
        stopButton.setOnClickListener {
            actionOnService(Actions.STOP)
        }
    }
    private fun actionOnService(action: Actions) {
        if (getServiceState(this) == ServiceState.STOPPED && action == Actions.STOP) return
        Intent(this, EndlessService::class.java).also {
            it.action = action.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(it)
                return
            }
            startService(it)
        }
    }
}