package com.ispgr5.locationsimulator.presentation.universalComponents

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ispgr5.locationsimulator.R

/**
Alert Dialog to Confirm the Deletion of an Item
 */
@Composable
fun ConfirmDeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
){
    AlertDialog(
        onDismissRequest = { onDismiss},
        text = {Text(stringResource(id = R.string.confirmDeleteDialogText))},
        title = {Text(stringResource(id = R.string.confirmDeleteDialogTitle))},
        confirmButton = {
            TextButton(onClick = onConfirm){
              Text(text = stringResource(id = R.string.confirmDeleteDialogConfirmBtn))
            } },
        dismissButton = {TextButton(onClick = onDismiss){
            Text(text = stringResource(id = R.string.confirmDeleteDialogDismissBtn))
        }}
    )
}