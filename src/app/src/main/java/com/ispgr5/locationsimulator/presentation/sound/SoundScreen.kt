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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.data.storageManager.SoundStorageManager
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.presentation.MainActivity

/**
 * Shows a list of Audio Files to be selected.
 */
@Composable
fun SoundScreen(
    navController: NavController,
    viewModel: SoundViewModel = hiltViewModel(),
    soundStorageManager: SoundStorageManager,
    mainActivity: MainActivity
) {
    val state = viewModel.state.value
    viewModel.onEvent(SoundEvent.RefreshPage(soundStorageManager = soundStorageManager))
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /**
         * The refresh Button
         */
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            onClick = {
                viewModel.onEvent(SoundEvent.RefreshPage(soundStorageManager = soundStorageManager))
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_refresh_24),
                contentDescription = null
            )
        }

        /**
         * Header Text
         */
        Text(text = stringResource(id = R.string.soundscreen_soundselection), fontSize = 30.sp)

        /**
         * import Button
         */
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            onClick = {
                viewModel.onEvent(SoundEvent.ImportSound(soundStorageManager = soundStorageManager))
            }
        ) {
            Text(text = stringResource(id = R.string.soundscreen_import))
        }

        /**
         * List of all known Sounds
         */
        LazyColumn {
            items(state.soundNames) { soundName ->
                SingleSound(
                    soundName,
                    onPlayClicked = {viewModel.onEvent(SoundEvent.TestPlaySound(mainActivity, soundName))},
                    onSelectClicked = {viewModel.onEvent(SoundEvent.SelectSound(soundName, navController))}
                )
            }
        }
    }
}