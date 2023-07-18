package com.ispgr5.locationsimulator.presentation.universalComponents

import androidx.compose.material.SnackbarDuration

data class SnackbarContent(
    val text: String,
    val actionLabel: String?,
    val snackbarDuration: SnackbarDuration
)