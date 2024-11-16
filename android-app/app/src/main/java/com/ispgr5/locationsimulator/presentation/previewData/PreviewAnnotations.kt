package com.ispgr5.locationsimulator.presentation.previewData

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes

@ThemePreview
@ScreenSizePreview
@FontSizePreviews
@LocalesPreview
annotation class AppPreview

@Preview(group = "theme", name = "Light")
@Preview(group = "theme", name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
annotation class ThemePreview

@Preview(group = "screen size", name = "Phone", device = "spec:width=411dp,height=891dp", showSystemUi = true)
@Preview(group = "screen size", name = "Phone - Landscape",
    device = "spec:width=411dp,height=891dp,orientation=landscape,dpi=420",
    showSystemUi = true)
@Preview(group = "screen size", name = "Unfolded Foldable", device = "spec:width=673dp,height=841dp", showSystemUi = true)
@Preview(group = "screen size", name = "Tablet", device = "spec:width=1280dp,height=800dp,dpi=240", showSystemUi = true)
@Preview(group = "screen size", name = "Desktop", device = "spec:width=1920dp,height=1080dp,dpi=160", showSystemUi = true)
annotation class ScreenSizePreview


@Preview(locale = "de", group = "locale", name = "de-DE")
@Preview(locale = "en", group = "locale", name = "en-US")
annotation class LocalesPreview

@Preview(group = "font size", name = "font 85%", fontScale = 0.85f)
@Preview(group = "font size", name = "font 100%", fontScale = 1.0f)
@Preview(group = "font size", name = "font 115%", fontScale = 1.15f)
@Preview(group = "font size", name = "font 130%", fontScale = 1.3f)
@Preview(group = "font size", name = "font 150%", fontScale = 1.5f)
@Preview(group = "font size", name = "font 180%", fontScale = 1.8f)
@Preview(group = "font size", name = "font 200%", fontScale = 2f)
annotation class FontSizePreviews