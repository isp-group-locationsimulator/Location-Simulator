package com.ispgr5.locationsimulator.presentation.universalComponents

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R

/**
 * general TopAppBar for all Screens with back Button
 */
@Composable
fun TopBar(navController : NavController,
           title: String,  //title of Screen
           backPossible : Boolean = true,  // Whether going back should be possible or not
           extraActions : @Composable()(RowScope.() -> Unit) = {},
) {
    if(backPossible) {
        // with back button
        TopAppBar(
            title = { Text(title) },
            navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "back Icon"
                        )
                    }
            },
            actions = extraActions,
        )
    }else{
        //no back Button
        //(work around) so that title is left when there is no nav icon
        TopAppBar(
            title = { Text(title) },
            actions = extraActions,
        )
    }
}