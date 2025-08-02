/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MediaPreviewItem(
    title: String,
    modifier: Modifier = Modifier,
    isFollowing: Boolean = false,
    coverImage: String? = null,
    ooClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        onClick = ooClick,
    ) {
        Box {
            AsyncImage(
                modifier = Modifier.aspectRatio(3f / 4f).fillMaxSize(),
                model = coverImage,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
            )
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .requiredHeight(50.dp)
                        .align(BottomStart)
                        .background(
                            brush =
                                Brush.verticalGradient(
                                    0f to Transparent,
                                    1f to Black.copy(0.8f),
                                ),
                        ),
            )
            Text(
                modifier = Modifier.align(BottomStart).padding(start = 8.dp, bottom = 12.dp),
                color = White,
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
            )

            if (isFollowing) {
                Box(
                    modifier =
                        Modifier
                            .align(TopEnd)
                            .padding(end = 8.dp),
                ) {
                    Icon(
                        modifier =
                            Modifier
                                .graphicsLayer {
                                    scaleY = 1.4f
                                    scaleX = 0.9f
                                },
                        imageVector = Icons.Filled.Bookmark,
                        tint = White,
                        contentDescription = null,
                    )
                    Icon(
                        modifier =
                            Modifier
                                .graphicsLayer {
                                    scaleY = 1.3f
                                    scaleX = 0.8f
                                },
                        imageVector = Icons.Filled.Bookmark,
                        tint = MaterialTheme.colorScheme.tertiary,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun MediaPreviewItemPreview() {
    MediaPreviewItem(
        modifier = Modifier.width(240.dp),
        isFollowing = true,
        title = "Preview Preview Preview Preview Preview Preview ",
    )
}
