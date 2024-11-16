package com.ispgr5.locationsimulator.presentation.universalComponents

import androidx.compose.material3.SnackbarDuration

data class SnackbarContent(
    val text: String,
    val snackbarDuration: SnackbarDuration,
    val withDismissAction: Boolean = false,
    val actionLabel: String? = null
) {
}