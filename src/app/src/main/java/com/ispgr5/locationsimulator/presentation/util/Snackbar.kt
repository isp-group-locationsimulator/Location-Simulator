package com.ispgr5.locationsimulator.presentation.util

import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import com.ispgr5.locationsimulator.presentation.universalComponents.SnackbarContent

@Composable
fun MakeSnackbar(scaffoldState: ScaffoldState, snackbarContent: MutableState<SnackbarContent?>) {
    LaunchedEffect(key1 = snackbarContent.value) {
        when (val snackbarValue = snackbarContent.value) {
            null -> return@LaunchedEffect
            else -> {
                scaffoldState.snackbarHostState.showSnackbar(
                    snackbarValue.text,
                    snackbarValue.actionLabel,
                    snackbarValue.snackbarDuration
                )
                snackbarContent.value = null
            }
        }
    }
}