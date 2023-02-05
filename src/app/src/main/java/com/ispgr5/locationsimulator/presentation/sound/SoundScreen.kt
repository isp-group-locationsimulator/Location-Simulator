package com.ispgr5.locationsimulator.presentation.sound

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.ispgr5.locationsimulator.FilePicker
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.presentation.MainActivity

/**
 * Shows a list of Audio Files to be selected.
 */
@Composable
fun SoundScreen(
    filePicker: FilePicker,
    mainActivity: MainActivity
) {
    val viewModel = SoundViewModel(filePicker)
    val state = viewModel.state
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            onClick = {
                viewModel.onEvent(SoundEvent.RefreshPage)
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_refresh_24),
                contentDescription = null
            )
        }
        Text(text = stringResource(id = R.string.soundscreen_soundselection), fontSize = 30.sp)
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            onClick = {
                viewModel.onEvent(SoundEvent.ImportSound)
            }
        ) {
            Text(text = stringResource(id = R.string.soundscreen_import))
        }
        LazyColumn {
            items(state.value.soundNames) { soundName ->
                SingleSound(soundName, viewModel, mainActivity)
            }
        }
    }
}