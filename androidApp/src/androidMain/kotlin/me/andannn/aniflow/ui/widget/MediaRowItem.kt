/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.widget

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import me.andannn.aniflow.ui.theme.AniflowTheme

@Composable
fun MediaRowItem(
    title: String,
    coverImage: String? = null,
    onClick: () -> Unit = {},
    onLongPress: (() -> Unit)? = null,
    centerInfoWidget: @Composable () -> Unit = {},
    titleMaxLines: Int = 2,
    showNewBadge: Boolean = false,
) {
    val colorScheme = MaterialTheme.colorScheme
    val surfaceTextColor = colorScheme.onSurfaceVariant
    val textStyle = MaterialTheme.typography

    Surface {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongPress,
                    ).padding(vertical = 8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Surface(
                modifier =
                    Modifier
                        .width(85.dp)
                        .height(IntrinsicSize.Min),
                shape = MaterialTheme.shapes.medium,
            ) {
                AsyncImage(
                    model = coverImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = title,
                        style = textStyle.titleMedium.copy(color = surfaceTextColor),
                        maxLines = titleMaxLines,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(end = 4.dp),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    centerInfoWidget.invoke()

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "TEMP info",
                        style = textStyle.bodySmall.copy(color = surfaceTextColor),
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                }

//            if (showNewBadge) {
//                Image(
//                    painter = painterResource(id = R.drawable.ic_new_badge),
//                    contentDescription = null,
//                    modifier =
//                        Modifier
//                            .size(36.dp)
//                            .align(Alignment.BottomEnd)
//                            .offset(x = 8.dp, y = 8.dp),
//                    colorFilter = ColorFilter.tint(Color.Red),
//                )
//            }
            }
        }
    }
}

@Composable
@Preview
private fun MediaListModelPreview() {
    AniflowTheme {
        MediaRowItem(
            title = "Sample Media Title",
            coverImage = "https://example.com/sample-cover.jpg",
            onClick = {},
            showNewBadge = true,
        )
    }
}
