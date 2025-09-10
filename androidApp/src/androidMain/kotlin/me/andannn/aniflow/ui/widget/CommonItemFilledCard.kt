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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import me.andannn.aniflow.ui.theme.AniflowTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CommonItemFilledCard(
    title: String,
    modifier: Modifier = Modifier,
    coverImage: String? = null,
    titleMaxLine: Int = Int.MAX_VALUE,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        shape = MaterialTheme.shapes.large,
        onClick = onClick,
    ) {
        Box {
            AsyncImage(
                modifier =
                    Modifier
                        .clip(MaterialTheme.shapes.large)
                        .background(Color.Gray)
                        .aspectRatio(3f / 4f)
                        .fillMaxSize(),
                model = coverImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp),
            text = title,
            maxLines = titleMaxLine,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(12.dp))
    }
}

@Preview
@Composable
private fun MediaItemFilledCardPreview() {
    AniflowTheme {
        Scaffold {
            CommonItemFilledCard(
                modifier = Modifier.width(120.dp).padding(it),
                title = "Title",
            )
        }
    }
}
