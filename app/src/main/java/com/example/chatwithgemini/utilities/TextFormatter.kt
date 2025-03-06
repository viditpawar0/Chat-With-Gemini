package com.example.chatwithgemini.utilities

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import java.util.regex.Matcher
import java.util.regex.Pattern

object TextFormatter {

    fun formatText(text: String): AnnotatedString {
        if (text.isBlank()) {
            return AnnotatedString(text)
        }

        val boldPattern = Pattern.compile("\\*\\*(.*?)\\*\\*")
        val matcher: Matcher = boldPattern.matcher(text)
        val annotatedString = buildAnnotatedString {
            var lastIndex = 0

            while (matcher.find()) {
                val start = matcher.start()
                val end = matcher.end()

                // Append text before the match
                append(text.substring(lastIndex, start))

                // Extract bold text without **
                val boldText = matcher.group(1) ?: ""
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black))
                append(boldText)
                pop()

                lastIndex = end
            }

            // Append remaining text
            if (lastIndex < text.length) {
                append(text.substring(lastIndex))
            }
        }

        return annotatedString
    }

    fun getBoldSpannableText(text: String): AnnotatedString {
        val boldPattern = Pattern.compile("\\*\\*(.*?)\\*\\*")
        val matcher: Matcher = boldPattern.matcher(text)
        return buildAnnotatedString {
            var lastIndex = 0

            while (matcher.find()) {
                val start = matcher.start()
                val end = matcher.end()
                append(text.substring(lastIndex, start))
                val boldText = matcher.group(1) ?: ""
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append(boldText)
                pop()
                lastIndex = end
            }
            if (lastIndex < text.length) {
                append(text.substring(lastIndex))
            }
        }
    }
}
