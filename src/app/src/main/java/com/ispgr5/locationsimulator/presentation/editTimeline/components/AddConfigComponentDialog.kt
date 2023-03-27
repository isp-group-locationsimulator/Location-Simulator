package com.ispgr5.locationsimulator.presentation.editTimeline.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ispgr5.locationsimulator.R

/**
 * Opens a Dialog which ask the user for pick between "Add Sound" or "Add Vibration"
 */
@Composable
fun AddConfigComponentDialog(
	onDismiss: () -> Unit,
	onNegativeClick: () -> Unit,
	onVibrationClicked: () -> Unit,
	onSoundClicked: () -> Unit
) {
	Dialog(onDismissRequest = onDismiss) {
		Card(
			//shadow around the Dialog Box
			elevation = 8.dp,
			shape = RoundedCornerShape(12.dp),
			backgroundColor = MaterialTheme.colors.surface
		) {
			Column(modifier = Modifier.padding(8.dp)) {

				Text(
					text = stringResource(id = R.string.editTimeline_AddConfigComponentDialog_Question),
					fontSize = 16.sp,
					modifier = Modifier.padding(8.dp)
				)
				Spacer(modifier = Modifier.height(8.dp))

				/**
				 * Sound and Vibration Select Buttons
				 */
				TextButton(onClick = onVibrationClicked) {
					Icon(
						painter = painterResource(id = R.drawable.ic_baseline_vibration_24),
						contentDescription = null,
						tint = MaterialTheme.colors.onSurface
					)
					Spacer(modifier = Modifier.width(8.dp))
					Text(
						text = stringResource(id = R.string.editTimeline_AddConfigComponentDialog_addVibration),
						fontSize = 18.sp,
						fontWeight = FontWeight.Bold,
						color = MaterialTheme.colors.onSurface

					)
				}
				TextButton(onClick = onSoundClicked) {
					Icon(
						painter = painterResource(id = R.drawable.audionouse2),
						contentDescription = null,
						tint = MaterialTheme.colors.onSurface
					)
					Spacer(modifier = Modifier.width(8.dp))
					Text(
						text = stringResource(id = R.string.editTimeline_addConfigComponentDialog_AddSound),
						fontSize = 18.sp,
						fontWeight = FontWeight.Bold,
						color = MaterialTheme.colors.onSurface
					)
				}

				/**
				 * cancel Button
				 */
				Row(
					horizontalArrangement = Arrangement.End,
					modifier = Modifier.fillMaxWidth()
				) {
					TextButton(onClick = onNegativeClick) {
						Text(text = stringResource(id = R.string.editTimeline_AddConfigComponentDialog_Cancel))
					}
				}
			}
		}
	}
}