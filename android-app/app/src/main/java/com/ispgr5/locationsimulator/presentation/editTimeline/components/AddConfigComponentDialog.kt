package com.ispgr5.locationsimulator.presentation.editTimeline.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags

/**
 * Opens a Dialog which ask the user for pick between "Add Sound" or "Add Vibration" and then
 * runns the onVibrationClicked or onSoundClicked function. Or the onDismiss or onNegativeClick
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
			elevation = CardDefaults.cardElevation(8.dp),
			shape = RoundedCornerShape(12.dp),
			colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
			modifier = Modifier.testTag(TestTags.EDIT_TIMELINE_SCREEN_ADD_DIALOG)
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
				TextButton(onClick = onVibrationClicked, modifier = Modifier.testTag(TestTags.EDIT_TIMELINE_SCREEN_ADD_DIALOG_VIBRATION)) {
					Icon(
						painter = painterResource(id = R.drawable.ic_baseline_vibration_24),
						contentDescription = null,
						tint = MaterialTheme.colorScheme.onSurface
					)
					Spacer(modifier = Modifier.width(8.dp))
					Text(
						text = stringResource(id = R.string.editTimeline_AddConfigComponentDialog_addVibration),
						fontSize = 18.sp,
						fontWeight = FontWeight.Bold,
						color = MaterialTheme.colorScheme.onSurface

					)
				}
				TextButton(onClick = onSoundClicked, modifier = Modifier.testTag(TestTags.EDIT_TIMELINE_SCREEN_ADD_DIALOG_SOUND)) {
					Icon(
						painter = painterResource(id = R.drawable.audionouse2),
						contentDescription = null,
						tint = MaterialTheme.colorScheme.onSurface
					)
					Spacer(modifier = Modifier.width(8.dp))
					Text(
						text = stringResource(id = R.string.editTimeline_addConfigComponentDialog_AddSound),
						fontSize = 18.sp,
						fontWeight = FontWeight.Bold,
						color = MaterialTheme.colorScheme.onSurface
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