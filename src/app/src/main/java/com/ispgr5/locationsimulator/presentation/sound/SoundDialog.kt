package com.ispgr5.locationsimulator.presentation.sound

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.ispgr5.locationsimulator.R

/**
 * Dialog for giving the recorded audio file a name
 */
@Composable
fun SoundDialog(
    popUpState: State<Boolean>,
    scaffoldState: ScaffoldState,
    onDismiss: (String) -> Unit
) {
	if (popUpState.value) {
		val text = remember { mutableStateOf("") }
		val textLength = remember { mutableStateOf(0) }
		AlertDialog(
			onDismissRequest = { /* Dismiss the Pop-Up window */ },
			title = { Text(text = stringResource(id = R.string.soundscreen_dialog_title)) },
			text = {
				TextField(
					value = text.value,
					onValueChange = {
						if (it.length <= 50) {
							textLength.value = it.length
							text.value = it
						}
					},
				)
			},
			confirmButton = {
				Button(
					enabled = text.value != "",
					onClick = {
						onDismiss(text.value)
					}) {
					Text(text = stringResource(id = R.string.soundscreen_dialog_button))
				}
			}
		)
	}
}
