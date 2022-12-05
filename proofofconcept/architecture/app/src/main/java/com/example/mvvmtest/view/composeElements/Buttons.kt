package com.example.mvvmtest.view.composeElements

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun NavigateButton(
    text: String,
    onClickButton: () -> Unit,
    navController:NavHostController
) {
    Button(
        onClick = {
            onClickButton()
            navController.navigate("sessionStart")}
    ) {
        Text(text = text)
    }
}