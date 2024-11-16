package com.ispgr5.locationsimulator.presentation.universalComponents

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import com.ispgr5.locationsimulator.core.util.TestTags

/**
 * general TopAppBar for all Screens with back Button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSimulatorTopBar(
    onBackClick: (() -> Unit)?,
    title: AnnotatedString,  //title of Screen
    backPossible: Boolean = true,  // Whether going back should be possible or not
    extraActions: @Composable (RowScope.() -> Unit) = {},
) {
    CenterAlignedTopAppBar(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSimulatorTopBar(
    onBackClick: (() -> Unit)?,
    title: String,  //title of Screen
    backPossible: Boolean = true,  // Whether going back should be possible or not
    extraActions: @Composable (RowScope.() -> Unit) = {},
) = LocationSimulatorTopBar(
    onBackClick=onBackClick,
    title = AnnotatedString(title),
    backPossible = backPossible,
    extraActions = extraActions
)