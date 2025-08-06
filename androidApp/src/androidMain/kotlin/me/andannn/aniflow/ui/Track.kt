/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import me.andannn.aniflow.components.track.TrackComponent
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem
import me.andannn.aniflow.ui.widget.MediaRowItem

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackContent(
    content: List<MediaWithMediaListItem>,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Track") },
            )
        },
    ) {
        LazyColumn(Modifier.padding(top = it.calculateTopPadding())) {
            items(
                items = content,
                key = { it.mediaListModel.id },
            ) { item ->
                val media = item.mediaModel
                MediaRowItem(
                    title = media.title?.romaji.toString(),
                    coverImage = media.coverImage,
                )
            }
        }
    }
}
