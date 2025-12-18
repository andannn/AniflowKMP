/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.widget

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.andannn.aniflow.ui.theme.EspecialMessageFontFamily
import kotlin.text.forEach

@Composable
fun InfoItemHorizon(
    icon: ImageVector,
    contentText: String,
    modifier: Modifier = Modifier,
) {
    val numberColor = MaterialTheme.colorScheme.primary
    Row(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            modifier = Modifier.wrapContentWidth(),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primaryFixedDim,
            )
            Spacer(modifier = Modifier.width(8.dp))
            val textSize = MaterialTheme.typography.labelMedium.fontSize
            val text =
                buildAnnotatedString {
                    contentText.forEach {
                        if (it.isDigit() || it == '#' || it == '%') {
                            withStyle(
                                SpanStyle(
                                    fontFamily = EspecialMessageFontFamily,
                                    fontSize = (textSize.value + 4).sp,
                                    color = numberColor,
                                ),
                            ) {
                                append(it)
                            }
                        } else {
                            append(it)
                        }
                    }
                }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}
