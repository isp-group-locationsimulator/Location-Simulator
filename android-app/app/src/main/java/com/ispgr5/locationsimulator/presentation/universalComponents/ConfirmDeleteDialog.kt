package com.ispgr5.locationsimulator.presentation.universalComponents

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ispgr5.locationsimulator.R

/**
Alert Dialog to Confirm the Deletion of an Item
 */
@Composable
fun ConfirmDeleteDialog(
    itemToDeleteName: String? = null,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val text = when(itemToDeleteName) {
        null -> stringResource(id = R.string.confirmDeleteDialogText)
        else -> stringResource(id = R.string.confirmDeleteDialogTextSpecific, itemToDeleteName)
    }
//    Dialog(onDismissRequest = { onDismiss }) {
//        Card(
//            elevation = 8.dp,
//            shape = RoundedCornerShape(12.dp),
//            backgroundColor = MaterialTheme.colors.surface,
//        ) {
//            Column(modifier = Modifier.padding(8.dp)) {
//                Text(
//                    text = stringResource(id = R.string.confirmDeleteDialogTitle)
//                )
//            }
//        }
//    }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        text = { Text(text) },
        title = { Text(stringResource(id = R.string.confirmDeleteDialogTitle)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.confirmDeleteDialogConfirmBtn))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.confirmDeleteDialogDismissBtn))
            }
        },
        shape = RoundedCornerShape(12.dp),
        backgroundColor = MaterialTheme.colors.surface
    )
}