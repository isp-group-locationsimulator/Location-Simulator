package com.ispgr5.locationsimulator.presentation.editTimeline.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
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

/**
 * Compose to show the Timeline of a Configuration
 * @param components all Configuration Components that the timeline should show
 * @param selectedComponent the Configuration Component that is currently selected
 * @param onSelectAComponent what should happen if a Time line Item gets clicked
 * @param onAddClicked functions which gets called if the add button gets clicked
 * @param showAddButton whether the AddButton should be drawn or not
 */
@Composable
fun Timeline(
    components: List<ConfigComponent>,
    selectedComponent: ConfigComponent?,
    onSelectAComponent: (ConfigComponent) -> Unit,
    onAddClicked: () -> Unit,
    showAddButton: Boolean = true
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
            .height(IntrinsicSize.Min)) {
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
            if (showAddButton) {
                Button(
                    elevation = ButtonDefaults.elevation(4.dp),
                    onClick = onAddClicked,
                    modifier = Modifier
                        .width(55.dp)
                        .height(55.dp)
                        .padding(6.dp)
                        .testTag(TestTags.EDIT_TIMELINE_SCREEN_ADD_BUTTON)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_add_24),
                        contentDescription = null
                    )
                }
            }
        }
        Scrollbars(state = scrollBarState)
    }

}

/**
 * Conmpose to draw a Item of the Timeline wich represents a ConfigComponent
 * @param isSelected is this Configuration Component currently selected?
 * @param configItem the Configuration Component that should showed
 * @param onSelect what should happen if this Time line Item gets clicked
 */
@Composable
fun TimelineItem(
    isSelected: Boolean,
    configItem: ConfigComponent,
    onSelect: (ConfigComponent) -> Unit
) {

    Card(elevation = 4.dp, backgroundColor = MaterialTheme.colors.surface,
        //if selected the Item gets a Border in another Color
        modifier = if (isSelected) {
            Modifier
                .width(55.dp)
                .height(55.dp)
                .padding(6.dp)
                .border(1.dp, MaterialTheme.colors.primary, RoundedCornerShape(10))
                .clickable { onSelect(configItem) }
                .testTag(TestTags.EDIT_CONFIG_ITEM)
        } else {
            Modifier
                .width(55.dp)
                .height(55.dp)
                .padding(6.dp)
                .clickable { onSelect(configItem) }
                .testTag(TestTags.EDIT_CONFIG_ITEM)
        }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(2.dp)
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