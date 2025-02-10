package com.ispgr5.locationsimulator.presentation.homescreen


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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

            // Zustand für die Sichtbarkeit der Bilder
            var showHelpImage1 by remember { mutableStateOf(false) }
            var showHelpImage2 by remember { mutableStateOf(false) }
            var showHelpImage3 by remember { mutableStateOf(false) }
            var showHelpImage4 by remember { mutableStateOf(false) }

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(scaffoldPadding)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 30.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    // Erste Hilfe-Sektion
                    val annotatedString1 = buildAnnotatedString {
                        pushStringAnnotation(tag = "help1", annotation = "help1")
                        withStyle(
                            style = SpanStyle(
                                color = Color.Blue,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append(stringResource(R.string.help_1))
                        }
                        pop()
                    }

                    ClickableText(
                        text = annotatedString1,
                        onClick = { offset ->
                            annotatedString1.getStringAnnotations("help1", offset, offset)
                                .firstOrNull()?.let {
                                    showHelpImage1 = !showHelpImage1 // Toggle visibility
                                }
                        }, modifier = Modifier.padding(top = 20.dp)

                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Zweite Hilfe-Sektion
                    val annotatedString2 = buildAnnotatedString {
                        pushStringAnnotation(tag = "help2", annotation = "help2")
                        withStyle(
                            style = SpanStyle(
                                color = Color.Blue,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append(stringResource(R.string.help_2))
                        }
                        pop()
                    }

                    ClickableText(
                        text = annotatedString2,
                        onClick = { offset ->
                            annotatedString2.getStringAnnotations("help2", offset, offset)
                                .firstOrNull()?.let {
                                    showHelpImage2 = !showHelpImage2 // Toggle visibility
                                }
                        },
                        modifier = Modifier.padding(top = 40.dp)

                    )

                    if (showHelpImage2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Image(
                                painter = painterResource(R.drawable.help_image3),
                                contentDescription = "",
                                modifier = Modifier
                                    .weight(1f)
                                    .width(400.dp)
                                    .height(400.dp)
                                    .clickable { showHelpImage2 = false },
                            )
                            Image(
                                painter = painterResource(R.drawable.help_image4),
                                contentDescription = "",
                                modifier = Modifier
                                    .weight(1f)
                                    .width(400.dp)
                                    .height(400.dp)
                                    .clickable { showHelpImage2 = false },
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Dritte Hilfe-Sektion
                    val annotatedString3 = buildAnnotatedString {
                        pushStringAnnotation(tag = "help3", annotation = "help3")
                        withStyle(
                            style = SpanStyle(
                                color = Color.Blue,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append(stringResource(R.string.help_3))
                        }
                        pop()
                    }

                    ClickableText(

                        text = annotatedString3,
                        onClick = { offset ->
                            annotatedString3.getStringAnnotations("help3", offset, offset)
                                .firstOrNull()?.let {
                                    showHelpImage3 = !showHelpImage3 // Toggle visibility
                                }
                        },
                        modifier = Modifier.padding(top = 60.dp)

                    )

                    if (showHelpImage1) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Image(
                                painter = painterResource(R.drawable.help_image1),
                                contentDescription = "",
                                modifier = Modifier
                                    .weight(1f)
                                    .width(400.dp)
                                    .height(400.dp)
                                    .clickable { showHelpImage1 = false },
                            )
                            Image(
                                painter = painterResource(R.drawable.help_image2),
                                contentDescription = "",
                                modifier = Modifier
                                    .weight(1f)
                                    .width(400.dp)
                                    .height(400.dp)
                                    .clickable { showHelpImage1 = false },
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }


                    if (showHelpImage3) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .zIndex(1f) // Höherer zIndex, um über allem zu liegen
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 30.dp, vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.help_image5),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .weight(1f)
                                        .width(400.dp)
                                        .height(400.dp)
                                        .clickable { showHelpImage3 = false },
                                )
                                Image(
                                    painter = painterResource(R.drawable.help_image6),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .weight(1f)
                                        .width(400.dp)
                                        .height(400.dp)
                                        .clickable { showHelpImage3 = false },
                                )
                                Image(
                                    painter = painterResource(R.drawable.help_image7),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .weight(1f)
                                        .width(400.dp)
                                        .height(400.dp)
                                        .clickable { showHelpImage3 = false },
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                        }
                    }

                    Scrollbars(state = scrollbarsState)


                    // Vierte  Hilfe-Sektion
                    val annotatedString4 = buildAnnotatedString {
                        pushStringAnnotation(tag = "help4", annotation = "help4")
                        withStyle(
                            style = SpanStyle(
                                color = Color.Blue,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append(stringResource(R.string.help_4))
                        }
                        pop()
                    }

                    ClickableText(
                        text = annotatedString4,
                        onClick = { offset ->
                            annotatedString4.getStringAnnotations("help4", offset, offset)
                                .firstOrNull()?.let {
                                    showHelpImage4 = !showHelpImage4 // Toggle visibility
                                }
                        }, modifier = Modifier.padding(top = 80.dp)

                    )
                    if (showHelpImage4) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Image(
                                painter = painterResource(R.drawable.help_image1),
                                contentDescription = "",
                                modifier = Modifier
                                    .weight(1f)
                                    .width(400.dp)
                                    .height(400.dp)
                                    .clickable { showHelpImage4 = false },
                            )
                            Image(
                                painter = painterResource(R.drawable.help_image8),
                                contentDescription = "",
                                modifier = Modifier
                                    .weight(1f)
                                    .width(400.dp)
                                    .height(400.dp)
                                    .clickable { showHelpImage4 = false },
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

    )
}
