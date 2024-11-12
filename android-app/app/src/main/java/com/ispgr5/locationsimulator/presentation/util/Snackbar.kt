package com.ispgr5.locationsimulator.presentation.util

import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import com.ispgr5.locationsimulator.presentation.universalComponents.SnackbarContent

@Composable
fun RenderSnackbarOnChange(
    snackbarHostState: SnackbarHostState,
    snackbarContent: MutableState<SnackbarContent?>
) {
    LaunchedEffect(key1 = snackbarContent.value) {
        when (val snackbarValue = snackbarContent.value) {
            null -> return@LaunchedEffect
            else -> {
                snackbarHostState.showSnackbar(
                    message = snackbarValue.text,
                    actionLabel = snackbarValue.actionLabel,
                    withDismissAction = snackbarValue.withDismissAction,
                    duration = snackbarValue.snackbarDuration
                )
                snackbarContent.value = null
            }
        }
    }
}

@Composable
fun AppSnackbarHost(snackbarHostState: SnackbarHostState) = SnackbarHost(
    hostState = snackbarHostState,
    snackbar = { data ->
        Snackbar(
            snackbarData = data,
            containerColor = colorScheme.primaryContainer,
            contentColor = colorScheme.onPrimaryContainer,
            dismissActionContentColor = colorScheme.onPrimaryContainer
        )
    }
)