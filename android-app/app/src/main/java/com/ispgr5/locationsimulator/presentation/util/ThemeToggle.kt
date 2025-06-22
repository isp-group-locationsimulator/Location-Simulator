package com.ispgr5.locationsimulator.presentation.util

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.presentation.homescreen.DynamicColorSchemeToggle
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import com.ispgr5.locationsimulator.ui.theme.ThemeType


@Composable
fun ThemeToggle(
    selectedTheme: ThemeState, onSetTheme: (ThemeState) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(id = R.string.homescreen_app_theme),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onBackground
        )
        MultiStateToggle(
            stateKeyLabelMap = ThemeType.entries.associateWith { theme -> theme.labelStringRes },
            selectedOption = selectedTheme.themeType,
            onSelectionChange = { newTheme ->
                onSetTheme(selectedTheme.copy(themeType = newTheme))
            })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            DynamicColorSchemeToggle(
                useDynamicColors = selectedTheme.useDynamicColor,
                onSelectionChange = { useDynamicColor ->
                    onSetTheme(selectedTheme.copy(useDynamicColor = useDynamicColor))
                }
            )
        }
    }
}