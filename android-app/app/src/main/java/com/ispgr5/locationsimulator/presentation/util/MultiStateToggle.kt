package com.ispgr5.locationsimulator.presentation.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.presentation.homescreen.conditional


@Composable
fun <K> MultiStateToggle(
    stateKeyLabelMap: Map<K, Int>, selectedOption: K, onSelectionChange: (K) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 4.dp,
        modifier = Modifier.wrapContentSize(),
        color = colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(24.dp))
                .background(colorScheme.surfaceContainer)
        ) {
            stateKeyLabelMap.entries.forEach { (key, labelStringRes) ->
                Text(
                    text = stringResource(id = labelStringRes),
                    color = when (key == selectedOption) {
                        true -> colorScheme.onPrimary
                        else -> colorScheme.onSurface
                    },
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(24.dp))
                        .clickable {
                            onSelectionChange(key)
                        }
                        .background(
                            when (key) {
                                selectedOption -> {
                                    colorScheme.primary
                                }

                                else -> {
                                    colorScheme.surfaceContainer
                                }
                            }
                        )
                        .padding(
                            vertical = 8.dp,
                            horizontal = 16.dp,
                        )
                        .conditional(labelStringRes == R.string.dark) {
                            testTag(TestTags.HOME_DARKMODE)
                        }
                        .conditional(labelStringRes == R.string.light) {
                            testTag(TestTags.HOME_LIGHTMODE)
                        }
                )
            }
        }
    }
}
