/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AppBarScope
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.andannn.aniflow.ui.theme.StyledReadingContentFontFamily

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun AppBarScope.iconItemWithLabel(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    listItemLabel: String,
    iconLabel: String? = null,
) {
    if (iconLabel == null) {
        clickableItem(
            onClick = onClick,
            icon = icon,
            label = listItemLabel,
        )
    } else {
        customItem(
            appbarContent = {
                Row(
                    modifier =
                        Modifier
                            .height(48.dp)
                            .clip(CircleShape)
                            .padding(4.dp)
                            .clickable {
                                onClick()
                            },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    icon()

                    val textSize =
                        MaterialTheme.typography.labelMedium.fontSize
                    val text =
                        buildAnnotatedString {
                            iconLabel.forEach {
                                withStyle(
                                    SpanStyle(
                                        fontFamily = StyledReadingContentFontFamily,
                                        fontSize = (textSize.value).sp,
                                    ),
                                ) {
                                    append(it)
                                }
                            }
                        }
                    Text(
                        modifier = Modifier.align(Alignment.Bottom).padding(bottom = 3.dp),
                        text = text,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            },
            menuContent = {
                ListItem(
                    leadingContent = { icon() },
                    headlineContent = { Text(listItemLabel) },
                    modifier = Modifier.clickable { onClick() },
                )
            },
        )
    }
}
