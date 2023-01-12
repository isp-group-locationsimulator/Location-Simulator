package com.ispgr5.locationsimulator.presentation.edit

import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.TextField
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.navigation.compose.rememberNavController
import com.ispgr5.locationsimulator.presentation.select.SelectViewModel

@Preview(name = "Preview1", device = Devices.PIXEL, showSystemUi = true)
@Composable
fun EditScreenPreview(){
    val navController = rememberNavController()
    EditScreen(navController = navController)
}

@Composable
fun EditScreen (
    navController: NavController,
    viewModel: EditViewModel = hiltViewModel()
){
    val state = viewModel.state.value
    Column( modifier = Modifier.fillMaxSize()) {
        Text("Duration")
        TextField(value = state.duration.toString(), onValueChange = {viewModel.onEvent(EditEvent.addDuration(it))})
        Text("Pause")
        TextField(value = state.pause.toString(), onValueChange = {viewModel.onEvent(EditEvent.addPause(it))})
        androidx.compose.material.Button(onClick =
        { viewModel.onEvent(EditEvent.save)
            navController.navigate("selectScreen")
        }
        ) {
        }
    }

}
