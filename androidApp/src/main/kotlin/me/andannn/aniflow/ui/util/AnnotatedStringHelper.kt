/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

fun AnnotatedString.Builder.appendItem(
    key: String,
    value: String,
) {
    withStyle(
        style =
            SpanStyle(
                fontWeight = FontWeight.W700,
            ),
    ) {
        append("$key: ")
    }
    append(value)
    append("\n")
}
