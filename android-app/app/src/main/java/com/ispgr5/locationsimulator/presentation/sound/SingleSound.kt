package com.ispgr5.locationsimulator.presentation.sound

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags

/**
 * Shows a single Audio File and a button to play it.
 */
@Composable
fun SingleSound(
    soundName: String,
    currentPlayingSoundName: String?,
    onPlayClicked: () -> Unit,
    onStopClicked: () -> Unit,
    onSelectClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
) {
    Row(Modifier.padding(horizontal = 5.dp)) {
        /**
         * Play this Sound Button
         */
        IconButton(
            modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max),
            onClick = when (currentPlayingSoundName) {
                soundName -> onStopClicked
                else -> onPlayClicked
            }
        ) {
            Icon(
                painter = painterResource(
                    when (currentPlayingSoundName) {
                        soundName -> R.drawable.ic_baseline_stop_24
                        else -> R.drawable.ic_baseline_play_arrow_24
                    }
                ),
                contentDescription = null
            )
        }

        /**
         * Button with the sound Name in it. Click to Select
         */
        Button(
            modifier = Modifier
                .height(intrinsicSize = IntrinsicSize.Max)
                .weight(1f)
                .padding(2.dp)
                .testTag(TestTags.SOUND_SELECT_BUTTON),
            onClick = onSelectClicked
        ) {
            Text(text = soundName)
        }

        /**
         * Button to delete the sound.
         */
        IconButton(
            modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max),
            onClick = onDeleteClicked
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_delete_outline_24),
                contentDescription = null
            )
        }
    }
}