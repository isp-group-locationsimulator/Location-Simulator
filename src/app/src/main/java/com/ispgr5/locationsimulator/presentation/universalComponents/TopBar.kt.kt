package com.ispgr5.locationsimulator.presentation.universalComponents

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R

/**
 * general TopAppBar for all Screens with back Button
 */
@Composable
fun TopBar( navController : NavController,
            title: String,  //title of Screen
            backPossible : Boolean = true  // Whether going back should be possible or not
) {
    TopAppBar(title = { Text(title) },
        navigationIcon = {
            if(backPossible) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = "back Icon"
                    )
                }
            }
        }
    )
}