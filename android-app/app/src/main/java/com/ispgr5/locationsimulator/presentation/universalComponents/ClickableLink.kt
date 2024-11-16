package com.ispgr5.locationsimulator.presentation.universalComponents

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

@Composable
fun ClickableLink(
    text: String, urlTarget: String,
    spanStyle: SpanStyle = ClickableLinkDefaults.defaultSpanStyle(),
    paragraphStyle: ParagraphStyle = ClickableLinkDefaults.defaultParagraphStyle()
) {
    val annotatedString = buildAnnotatedString {
        withStyle(paragraphStyle) {
            withStyle(spanStyle) {
                append(text)
                addLink(
                    url = LinkAnnotation.Url(urlTarget),
                    start = 0,
                    end = text.length -1
                )
            }
        }

    }

    Text(annotatedString)
}

object ClickableLinkDefaults {
    @Composable
    fun defaultSpanStyle() = SpanStyle(
        color = colorScheme.primary,
        textDecoration = TextDecoration.Underline,
        fontSize = MaterialTheme.typography.bodyLarge.fontSize
    )

    @Composable
    fun defaultParagraphStyle() = ParagraphStyle(
        textAlign = TextAlign.Center
    )
}