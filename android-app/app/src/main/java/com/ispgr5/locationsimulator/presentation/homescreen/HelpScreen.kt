package com.ispgr5.locationsimulator.presentation.homescreen


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gigamole.composescrollbars.Scrollbars
import com.gigamole.composescrollbars.config.ScrollbarsConfig
import com.gigamole.composescrollbars.config.ScrollbarsOrientation
import com.gigamole.composescrollbars.rememberScrollbarsState
import com.gigamole.composescrollbars.scrolltype.ScrollbarsScrollType
import com.gigamole.composescrollbars.scrolltype.knobtype.ScrollbarsStaticKnobType
import com.ispgr5.locationsimulator.BuildConfig
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.presentation.universalComponents.ClickableLink
import com.ispgr5.locationsimulator.presentation.universalComponents.ClickableLinkDefaults
import com.ispgr5.locationsimulator.presentation.universalComponents.LocationSimulatorTopBar
import com.ispgr5.locationsimulator.presentation.util.Screen
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import com.ispgr5.locationsimulator.ui.theme.ThemeType


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
                Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
            ) {


                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 30.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {

                        //hypertext erstellen für die Fragen
                        val annotatedString = buildAnnotatedString {
                            pushStringAnnotation(tag = "help", annotation = "help")
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
                            text = annotatedString,
                            onClick = { offset ->
                                annotatedString.getStringAnnotations("help", offset, offset)
                                    .firstOrNull()?.let {
                                        //showImages = true
                                    }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Bilder erscheinen, wenn showImages = true
//                        if (showImages) {
//                            Image(
//                                painter = painterResource(id = R.drawable.help_image),
//                                contentDescription = "Hilfe-Bild",
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(200.dp)
//                                    .clip(RoundedCornerShape(8.dp))
//                            )
//                        }
//                    }
                    }


                    Scrollbars(state = scrollbarsState)
                }
            }
        }
    )
}
