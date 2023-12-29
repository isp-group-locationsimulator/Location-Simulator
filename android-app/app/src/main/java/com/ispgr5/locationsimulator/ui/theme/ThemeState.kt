package com.ispgr5.locationsimulator.ui.theme

import com.ispgr5.locationsimulator.R

/**
 * The Theme State to control whether the Dark Mode is On or not
 */
data class ThemeState (
    val themeType: ThemeType = ThemeType.LIGHT //whether the dark mode is on or not (Light Mode is standard)
)

enum class ThemeType(val labelStringRes: Int) {
    LIGHT(R.string.light),
    AUTO(R.string.auto),
    DARK(R.string.dark)
}