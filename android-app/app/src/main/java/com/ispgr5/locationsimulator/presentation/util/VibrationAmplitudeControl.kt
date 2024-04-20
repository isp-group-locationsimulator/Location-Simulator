package com.ispgr5.locationsimulator.presentation.util

import android.content.Context
import android.os.Build
import android.os.Vibrator
import com.ispgr5.locationsimulator.R

val Context.vibrator: Vibrator get() = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        // Use the recommended method to get the Vibrator service
        this.getSystemService(Vibrator::class.java)
    }
    else -> {
        // Use the deprecated method to get the Vibrator service
        @Suppress("DEPRECATION")
        this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
}

val Context.vibratorHasAmplitudeControlAndReason: Pair<Boolean, Int?> get() {
    return when {
        Build.VERSION.SDK_INT <= Build.VERSION_CODES.O -> false to R.string.android_to_old_oreo_required
        else -> {
            when (vibrator.hasAmplitudeControl()) {
                true -> true to null
                else -> false to R.string.android_current_but_no_amplitude_control
            }
        }
    }


}