package com.ispgr5.locationsimulator.presentation.homescreen


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Scaffold
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
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
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData
import com.ispgr5.locationsimulator.presentation.universalComponents.LocationSimulatorTopBar
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import kotlinx.coroutines.launch


@Composable

fun HelpScreen(navController: NavController,
               appTheme: MutableState<ThemeState>
) {



        HelpScreenScaffold(
            onBackClick = { navController.popBackStack() },
            appTheme = appTheme
        )


}

@Composable
@Preview
fun HelpScreenPreview() {
    val themeState = remember {
        mutableStateOf(PreviewData.themePreviewState)
    }
    LocationSimulatorTheme {
        HelpScreenScaffold(
            onBackClick = {},
            appTheme = themeState)
    }
}
@Composable
fun HelpScreenScaffold(
    onBackClick: () -> Unit,
    appTheme: MutableState<ThemeState>
) { Scaffold(
        topBar = {
            LocationSimulatorTopBar(
                onBackClick = onBackClick,
                title = stringResource(id = R.string.ScreenHelp)
            )
        },
        content = { scaffoldPadding ->
            val isGerman = LocalContext.current.resources.configuration.locale.language == "de"
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
                    imageIds = if (isGerman)
                        listOf(R.drawable.help_image1_de, R.drawable.help_image2_de)
                    else
                        listOf(R.drawable.help_image1_en, R.drawable.help_image2_en)
                )
                HelpCard(
                    title = stringResource(R.string.help_2),
                    imageIds = if (isGerman)
                        listOf(R.drawable.help_image3_de, R.drawable.help_image4_de)
                    else
                        listOf(R.drawable.help_image3_en, R.drawable.help_image4_en)
                )
                HelpCard(
                    title = stringResource(R.string.help_3),
                    //TODO Images 6 and 7 still need to be translated and updated, because they show an older version of the app
                    imageIds = if (isGerman)
                        listOf(R.drawable.help_image5_de, R.drawable.help_image6_de, R.drawable.help_image7_de)
                    else
                        listOf(R.drawable.help_image5_en, R.drawable.help_image6_de, R.drawable.help_image7_de)
                )
                HelpCard(
                    title = stringResource(R.string.help_4),
                    imageIds = if (isGerman)
                        listOf(R.drawable.help_image1_de, R.drawable.help_image8_de)
                    else
                        listOf(R.drawable.help_image1_en, R.drawable.help_image8_en)
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
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceContainerHigh,
            contentColor = colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = colorScheme.onBackground,
                fontSize = 16.sp,
                textDecoration = TextDecoration.Underline
            )
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
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

                    // Linker Pfeil (nur anzeigen, wenn nicht auf Seite 0)
                    if (pagerState.currentPage > 0) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Vorheriges Bild",
                            )
                        }
                    }

                    // Rechter Pfeil (nur anzeigen, wenn weitere Seiten existieren)
                    if (pagerState.currentPage < imageIds.size - 1) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Nächstes Bild",
                            )
                        }
                    }
                }
            }
        }
    }
}

