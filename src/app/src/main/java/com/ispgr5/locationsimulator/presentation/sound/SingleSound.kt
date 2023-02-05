package com.ispgr5.locationsimulator.presentation.sound

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.presentation.MainActivity

/**
 * Shows a single Audio File and a button to play it.
 */
@Composable
fun SingleSound(soundName: String, viewModel: SoundViewModel, mainActivity: MainActivity) {
    Row {
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            onClick = {
                viewModel.onEvent(SoundEvent.TestPlaySound(mainActivity, soundName))
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                contentDescription = null
            )
        }
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            onClick = {
                viewModel.onEvent(SoundEvent.SelectSound(soundName))
            }
        ) {
            Text(text = soundName)
        }
    }
}