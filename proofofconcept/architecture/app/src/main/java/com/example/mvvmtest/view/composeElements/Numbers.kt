package com.example.mvvmtest.view.mainScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType


@Composable
fun MyNumberField(
    description: String,
    number: MutableState<Int>,
    onValueChanges: (numberValue: String) -> Unit
) {
    Column() {
        Text(text = description)
        TextField(
            value = number.value.toString(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
            onValueChange = {
                onValueChanges(it)
            }
        )
    }
}
