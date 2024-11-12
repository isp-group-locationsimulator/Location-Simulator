package com.ispgr5.locationsimulator.presentation.util

import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.presentation.editTimeline.isEdgeToEdgeEnabled
import com.ispgr5.locationsimulator.presentation.run.BackPressHandler
import com.ispgr5.locationsimulator.presentation.universalComponents.SnackbarContent

@Composable
fun BackPressGestureDisabler(
    snackbarContentState: MutableState<SnackbarContent?>
) {
    val context = LocalContext.current
    val isGestureModeEnabled = remember {
        isEdgeToEdgeEnabled(context)
    }
    if (isGestureModeEnabled > 0) {
        BackPressHandler {
            snackbarContentState.value = SnackbarContent(
                text = context.getString(R.string.the_back_gesture_is_disabled_please_use_the_bottom_above),
                snackbarDuration = SnackbarDuration.Short,
                withDismissAction = true
            )
        }
    }
}