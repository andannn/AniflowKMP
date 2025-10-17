/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.widget

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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import me.andannn.aniflow.data.getUserTitleString
import me.andannn.aniflow.data.infoString
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem
import me.andannn.aniflow.data.releasingTimeString

@Composable
fun MediaListRowItem(
    item: MediaWithMediaListItem,
    userTitleLanguage: UserTitleLanguage,
    shape: Shape,
    modifier: Modifier = Modifier,
    titleMaxLines: Int = 2,
    onClick: () -> Unit = {},
) {
    val colorScheme = MaterialTheme.colorScheme
    val surfaceTextColor = colorScheme.onSurfaceVariant

    Surface(
        modifier = modifier,
        shape = shape,
        onClick = onClick,
    ) {
        Row(
            modifier =
                Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Card(
                modifier =
                    Modifier.width(85.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                Box(Modifier.fillMaxSize()) {
                    AsyncImage(
                        modifier = Modifier.matchParentSize(),
                        model = item.mediaModel.coverImage,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                ) {
                    val title by rememberUpdatedState(
                        item.mediaModel.title.getUserTitleString(userTitleLanguage),
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(color = surfaceTextColor),
                        maxLines = titleMaxLines,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(end = 4.dp),
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val primaryColor = MaterialTheme.colorScheme.primary
                    val centerText =
                        remember(item) {
                            val progress = item.mediaListModel.progress
                            val episodes = item.mediaModel.episodes
                            val score = item.mediaListModel.score
                            val text =
                                buildString {
                                    if (progress != null && episodes != null && episodes > 0) {
                                        append("Ep $progress / $episodes")
                                    }

                                    if (score != null && score > 0) {
                                        if (isNotEmpty()) append(" • ")
                                        append("Score $score")
                                    }
                                }
                            buildSpecialMessageText(
                                text,
                                primaryColor,
                            )
                        }

                    Text(text = centerText)

                    Spacer(modifier = Modifier.height(16.dp))

                    val info =
                        remember(item.mediaModel) {
                            item.mediaModel.infoString()
                        }
                    Text(
                        text = info,
                        style = MaterialTheme.typography.bodySmall.copy(color = surfaceTextColor),
                    )
                }
            }
        }
    }
}
