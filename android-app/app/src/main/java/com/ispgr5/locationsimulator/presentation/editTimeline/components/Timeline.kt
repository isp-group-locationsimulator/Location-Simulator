package com.ispgr5.locationsimulator.presentation.editTimeline.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gigamole.composescrollbars.Scrollbars
import com.gigamole.composescrollbars.config.ScrollbarsConfig
import com.gigamole.composescrollbars.config.ScrollbarsGravity
import com.gigamole.composescrollbars.config.ScrollbarsOrientation
import com.gigamole.composescrollbars.rememberScrollbarsState
import com.gigamole.composescrollbars.scrolltype.ScrollbarsScrollType
import com.gigamole.composescrollbars.scrolltype.knobtype.ScrollbarsStaticKnobType
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.presentation.previewData.AppPreview
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme

/**
 * Compose to show the Timeline of a Configuration
 * @param components all Configuration Components that the timeline should show
 * @param selectedComponent the Configuration Component that is currently selected
 * @param onSelectAComponent what should happen if a Time line Item gets clicked
 * @param onAddClicked functions which gets called if the add button gets clicked
 * @param interactive whether the AddButton and scroll bar should be drawn or not
 */
@Composable
fun Timeline(
    components: List<ConfigComponent>,
    selectedComponent: ConfigComponent?,
    onSelectAComponent: ((ConfigComponent) -> Unit)?,
    onAddClicked: (() -> Unit)?,
    interactive: Boolean = true
) {

    val scrollState = rememberScrollState()
    val scrollBarState = rememberScrollbarsState(
        config = ScrollbarsConfig(
            orientation = ScrollbarsOrientation.Horizontal,
            gravity = ScrollbarsGravity.Start
        ),
        scrollType = ScrollbarsScrollType.Scroll(
            knobType = ScrollbarsStaticKnobType.Auto(),
            state = scrollState
        )
    )

    //iterate through all Configuration components and Display it with a TimelineItem
    Column(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row(
            modifier = Modifier.horizontalScroll(scrollState)
        ) {
            components.forEach { configComponent ->
                TimelineItem(
                    isSelected = selectedComponent === configComponent,
                    configItem = configComponent,
                    onSelect = onSelectAComponent
                )
            }
            /**
             * Add new Timeline Item(Configuration Component)
             */
            /**
             * Add new Timeline Item(Configuration Component)
             */
            if (interactive) {
                ElevatedCard(
                    shape = CircleShape,
                    modifier = Modifier
                        .width(55.dp)
                        .height(55.dp)
                        .padding(6.dp)
                        .clickable(onClick = onAddClicked!!)
                        .testTag(TestTags.EDIT_TIMELINE_SCREEN_ADD_BUTTON),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.primaryContainer,
                        contentColor = colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        modifier = Modifier.fillMaxSize(),
                        contentDescription = stringResource(id = R.string.add_configuration_component),
                    )
                }
            }
        }
        if (interactive) {
            Scrollbars(state = scrollBarState)
        }
    }

}

/**
 * Conmpose to draw a Item of the Timeline which represents a ConfigComponent
 * @param isSelected is this Configuration Component currently selected?
 * @param configItem the Configuration Component that should showed
 * @param onSelect what should happen if this Time line Item gets clicked
 */
@Composable
fun TimelineItem(
    isSelected: Boolean,
    configItem: ConfigComponent,
    onSelect: ((ConfigComponent) -> Unit)?
) {
    val baseModifier =
        Modifier
            .width(55.dp)
            .height(55.dp)
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .testTag(TestTags.EDIT_CONFIG_ITEM)
    val clickableModifier = onSelect?.let {
        baseModifier.clickable {
            onSelect(configItem)
        }
    } ?: baseModifier
    ElevatedCard(
        //if selected the Item gets a Border in another Color
        colors = CardDefaults.elevatedCardColors(
            containerColor = colorScheme.surfaceContainerHigh,
            contentColor = colorScheme.onSurface,
        ),
        modifier = when (isSelected) {
            true -> clickableModifier.border(
                1.5.dp,
                colorScheme.secondary,
                shape = CardDefaults.shape
            )

            else -> clickableModifier
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(2.dp)
                .fillMaxSize()
        ) {
            Icon(
                painter = if (configItem is ConfigComponent.Sound) {
                    painterResource(id = R.drawable.audionouse2)
                } else {
                    painterResource(id = R.drawable.ic_baseline_vibration_24)
                },
                contentDescription = null
            )

        }
    }
}

@Composable
@AppPreview
fun TimelinePreview() {
    LocationSimulatorTheme {
        Timeline(
            components = PreviewData.previewConfigurations.first().components,
            selectedComponent = PreviewData.previewConfigurations.first().components.first(),
            onSelectAComponent = {},
            onAddClicked = {},
            interactive = true
        )
    }
}