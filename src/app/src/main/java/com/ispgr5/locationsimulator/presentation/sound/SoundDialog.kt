package com.ispgr5.locationsimulator.presentation.sound

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
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
	onDismiss: (String) -> Unit
) {
	if (popUpState.value) {
		val text: MutableState<String> = remember { mutableStateOf("") }
		val textLength = remember { mutableIntStateOf(0) }
		AlertDialog(
			onDismissRequest = { /* Dismiss the Pop-Up window */ },
			title = { Text(text = stringResource(id = R.string.soundscreen_dialog_title)) },
			text = {
				TextField(
					value = text.value,
					onValueChange = { newText ->
						if (newText.length <= 50) {
							textLength.intValue = newText.length
							text.value = newText
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
