package com.example.mvvmtest.view.mainScreen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.mvvmtest.viewModel.MainViewModel

@Composable
fun toastMessage(message:String) {
    val context = LocalContext.current
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

}

@Composable
fun SelectScreen(
    mainViewModel: MainViewModel
) {
    Column() {
        MyNumberField(
            description = "Duration Vibration",
            number = mainViewModel.currentConfiguration.durationVibrateInSec,
            onValueChanges = mainViewModel::onDurationVibrationChanges
        )
        MyNumberField(
            description = "Duration Vibration",
            number = mainViewModel.currentConfiguration.durationVibrateInSec,
            onValueChanges = mainViewModel::onDurationVibrationChanges
        )
    }
}




