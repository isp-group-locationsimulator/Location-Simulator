package com.ispgr5.locationsimulator.presentation.homescreen


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gigamole.composescrollbars.Scrollbars
import com.gigamole.composescrollbars.config.ScrollbarsConfig
import com.gigamole.composescrollbars.config.ScrollbarsOrientation
import com.gigamole.composescrollbars.rememberScrollbarsState
import com.gigamole.composescrollbars.scrolltype.ScrollbarsScrollType
import com.gigamole.composescrollbars.scrolltype.knobtype.ScrollbarsStaticKnobType
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.presentation.universalComponents.LocationSimulatorTopBar
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme



@Composable

fun HelpScreen(navController: NavController,){



        HelpScreenScaffold {
            navController.popBackStack()
        }
    }

@Composable
@Preview
fun HelpScreenPreview() {
    LocationSimulatorTheme {
        HelpScreenScaffold(onBackClick = {})
    }
}
@Composable
fun HelpScreenScaffold(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            LocationSimulatorTopBar(
                onBackClick = onBackClick,
                title = stringResource(id = R.string.ScreenHelp)
            )
        },
        content = { scaffoldPadding ->
            val scrollState = rememberScrollState()
            val scrollbarsState = rememberScrollbarsState(
                config = ScrollbarsConfig(orientation = ScrollbarsOrientation.Vertical),
                scrollType = ScrollbarsScrollType.Scroll(
                    knobType = ScrollbarsStaticKnobType.Auto(),
                    state = scrollState
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                HelpCard(
                    title = stringResource(R.string.help_1),
                    imageIds = listOf(R.drawable.help_image1, R.drawable.help_image2)
                )
                HelpCard(
                    title = stringResource(R.string.help_2),
                    imageIds = listOf(R.drawable.help_image3, R.drawable.help_image4)
                )
                HelpCard(
                    title = stringResource(R.string.help_3),
                    imageIds = listOf(R.drawable.help_image5, R.drawable.help_image6, R.drawable.help_image7)
                )
                HelpCard(
                    title = stringResource(R.string.help_4),
                    imageIds = listOf(R.drawable.help_image1, R.drawable.help_image8)
                )
            }

            Scrollbars(state = scrollbarsState)
        }
    )
}

@Composable
fun HelpCard(title: String, imageIds: List<Int>) {
    var expanded by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState { imageIds.size }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDAD5))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.Black,
                fontSize = 16.sp,
                textDecoration = TextDecoration.Underline
            )
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth()
                ) { page ->
                    Image(
                        painter = painterResource(imageIds[page]),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp) // Größere Bilder
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }
        }
    }
}
