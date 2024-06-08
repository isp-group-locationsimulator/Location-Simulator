package com.ispgr5.locationsimulator.presentation.universalComponents

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.ispgr5.locationsimulator.core.util.TestTags

/**
 * general TopAppBar for all Screens with back Button
 */
@Composable
fun TopBar(
    onBackClick: (() -> Unit)?,
    title: String,  //title of Screen
    backPossible: Boolean = true,  // Whether going back should be possible or not
    extraActions: @Composable (RowScope.() -> Unit) = {},
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (backPossible) {
                IconButton(
                    onClick = { onBackClick!!.invoke() },
                    modifier = Modifier.testTag(TestTags.TOP_BAR_BACK_BUTTON)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back Icon"
                    )
                }
            }
        },
        actions = extraActions,
    )
}