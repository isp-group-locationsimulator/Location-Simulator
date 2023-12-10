package com.ispgr5.locationsimulator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

/**
 * Colors for the light Mode
 */
private val LightThemeColors = lightColors(
	primary = theBlue,
	primaryVariant = primaryLightVariant,
	onPrimary = white,
	secondary = secondaryLight,
	secondaryVariant = secondaryDark,
	onSecondary = black,
	error = RedErrorDark,
	onError = RedErrorLight,
	onBackground = black,
	surface = theBlueVeryLight,
	onSurface = black,
	)

/**
 * Colors for the Dark Mode
 */
private val DarkThemeColors = darkColors(
	primary = purple,
	primaryVariant = primaryDarkVariant,
	onPrimary = black,
	secondary = secondaryLight,
	secondaryVariant = secondaryDark,
	onSecondary = white,
	error = RedErrorLight,
	onError = RedErrorLight,
	onBackground = white,
	surface = LighterBlack,
	onSurface = white
)

/**
 * Theme chooser for Light and Dark Mode
 */
@Composable
fun LocationSimulatorTheme(
	themeState: ThemeState,
	content: @Composable () -> Unit,
) {
	MaterialTheme(
		colors = when (themeState.themeType) {
			ThemeType.DARK -> DarkThemeColors
			ThemeType.LIGHT -> LightThemeColors
			else -> when (isSystemInDarkTheme()) {
				true -> DarkThemeColors
				else -> LightThemeColors
			}
		},
		content= content
	)
}