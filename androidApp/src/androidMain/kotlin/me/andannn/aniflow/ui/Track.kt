/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import me.andannn.aniflow.components.track.TrackComponent
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem

@Composable
fun Track(
    component: TrackComponent,
    modifier: Modifier = Modifier,
) {
    val content by component.content.subscribeAsState()
    TrackContent(
        content = content.items,
        modifier = modifier,
    )
}

@Composable
fun TrackContent(
    content: List<MediaWithMediaListItem>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier) {
        items(
            content,
            key = { it.hashCode() },
        ) { item ->
            Text(item.toString())
            Divider()
        }
    }
}
