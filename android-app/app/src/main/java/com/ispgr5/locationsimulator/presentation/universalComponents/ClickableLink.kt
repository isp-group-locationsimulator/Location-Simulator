package com.ispgr5.locationsimulator.presentation.universalComponents

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
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
    val uriHandler = LocalUriHandler.current
    val annotatedString = buildAnnotatedString {
        withStyle(paragraphStyle) {
            withStyle(spanStyle) {
                append(text)
                addStringAnnotation(
                    tag = "uri",
                    annotation = urlTarget,
                    start = 0,
                    end = text.length - 1
                )
            }
        }

    }

    ClickableText(annotatedString, onClick = { offset ->
        annotatedString.getStringAnnotations("uri", offset, offset).firstOrNull()
            ?.let { annotation ->
                uriHandler.openUri(annotation.item)
            }
    })
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