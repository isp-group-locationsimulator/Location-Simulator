package com.ispgr5.locationsimulator.presentation.edit.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

/**
 * This composable creates a Number Field
 * with a description Text above
 * The input opens a Number Keyboard
 */
@Composable
fun MyNumberField(
	//The Text shown over the Number Field
	description: String,
	//The Number shown in the Number Field
	number: Int,
	//The Function that will be called by a change
	onValueChanges: (String) -> Unit
) {
	Column {
		Text(text = description)
		TextField(
			value = number.toString(),
			modifier = Modifier.fillMaxWidth(),
			keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
			onValueChange = onValueChanges
		)
	}
}