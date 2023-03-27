package com.ispgr5.locationsimulator.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

/*private val DarkColorPalette = darkColors(
	primary = Purple200,
	primaryVariant = Purple700,
	secondary = Teal200
)

private val LightColorPalette = lightColors(
	primary = theBlue,
	primaryVariant = theBlueLighter,
	secondary = Teal200,
	surface = theBlueVeryLight,
	 Other default colors to override
	background = Color.White,
	surface = Color.White,
	onPrimary = Color.White,
	onSecondary = Color.Black,
	onBackground = Color.Black,
	onSurface = Color.Black,

)

@Composable
fun LocationSimulatorTheme(
	darkTheme: Boolean,//isSystemInDarkTheme(),
	content: @Composable () -> Unit
) {
	val colors = if (darkTheme) {
		DarkColorPalette
	} else {
		LightColorPalette
	}

	MaterialTheme(
		colors = colors,
		typography = Typography,
		shapes = Shapes,
		content = content
	)
}
private val LightThemeColors = lightColors(
	primary = primaryLight,
	primaryVariant = primaryLightVariant,
	onPrimary = Black2,
	secondary = lightSecondary,
	secondaryVariant = lightSecondaryVariant,
	onSecondary = Black2,
	error = RedErrorDark,
	onError = RedErrorLight,

	)

private val DarkThemeColors = darkColors(
	primary = primaryDark,
	primaryVariant = primaryDarkVariant,
	onPrimary = White2,
	secondary = darkSecondary,
	secondaryVariant = darkSecondaryVariant,
	onSecondary = White2,
	error = RedErrorLight,
	onError = RedErrorLight,
	//surface = Color(0xFF3c506b),


)*/
private val LightThemeColors = lightColors(
	primary = theBlue,
	primaryVariant = primaryLightVariant,
	onPrimary = White2,
	secondary = secondaryLight,
	secondaryVariant = secondaryDark,
	onSecondary = Black2,
	error = RedErrorDark,
	onError = RedErrorLight,
	onBackground = Black2,
	surface = theBlueVeryLight,
	onSurface = Black2
	)

private val DarkThemeColors = darkColors(
	primary = LightBlack,
	primaryVariant = primaryDarkVariant,
	onPrimary = White2,
	secondary = secondaryLight,
	secondaryVariant = secondaryDark,
	onSecondary = White2,
	error = RedErrorLight,
	onError = RedErrorLight,
	onBackground = White2,
	surface = LighterBlack,
	onSurface = White2


)

@Composable
fun LocationSimulatorTheme(
	darkTheme: MutableState<ThemeState>,
	content: @Composable () -> Unit,
) {
	MaterialTheme(
		colors = if (darkTheme.value.isDarkTheme) DarkThemeColors else LightThemeColors,
		content= content
	)
}