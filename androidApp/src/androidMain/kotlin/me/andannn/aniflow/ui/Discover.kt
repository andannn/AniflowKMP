/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import kotlinx.coroutines.delay
import me.andannn.aniflow.components.discover.CategoryWithContents
import me.andannn.aniflow.components.discover.DiscoverComponent
import me.andannn.aniflow.data.model.MediaCategory
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.UserModel
import me.andannn.aniflow.ui.widget.MediaPreviewItem

@Composable
fun Discover(
    component: DiscoverComponent,
    modifier: Modifier = Modifier,
) {
    val categoryDataMap by component.categoryDataMap.subscribeAsState()
    val authedUser by component.authedUser.subscribeAsState()

    LaunchedEffect(Unit) {
        delay(2000)
        component.onStartLoginProcess()
    }
    DiscoverContent(
        categoryDataList = categoryDataMap.content,
        authedUser = authedUser.value,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverContent(
    categoryDataList: List<CategoryWithContents>,
    authedUser: UserModel?,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Discover") },
                actions = {
                    IconButton(
                        onClick = {},
                    ) {
                        if (authedUser != null) {
                            AsyncImage(
                                model = authedUser.avatar,
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds,
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                            )
                        }
                    }
                },
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier.padding(top = it.calculateTopPadding()),
        ) {
            items(
                items = categoryDataList,
                key = { it.category },
            ) { (category, items) ->
                TitleWithContent(
                    modifier = Modifier.fillMaxWidth(),
                    title = category.title,
                ) {
                    MediaPreviewSector(
                        mediaList = items,
                        onMediaClick = {},
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaPreviewSector(
    mediaList: List<MediaModel>,
    modifier: Modifier = Modifier,
    onMediaClick: (MediaModel) -> Unit = {},
) {
    val isLoading by rememberUpdatedState(mediaList.isEmpty())

    LazyRow(
        modifier = modifier.fillMaxWidth(),
    ) {
        if (isLoading) {
            repeat(6) {
                item {
                    Surface(
                        modifier = Modifier.width(240.dp).aspectRatio(3 / 4f),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                    ) {}
                }
            }
        } else {
            items(
                mediaList,
                key = { it.id },
            ) {
                MediaPreviewItem(
                    modifier = Modifier.width(240.dp),
                    title = it.title?.english ?: "EEEEEEEEEE",
                    isFollowing = false,
                    coverImage = it.coverImage,
                    ooClick = { onMediaClick(it) },
                )
            }
        }
    }
}

@Composable
fun TitleWithContent(
    title: String,
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Column(modifier) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title, maxLines = 1, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.weight(1f))
            TextButton(onMoreClick) {
                Text("More")
            }
        }
        content()
    }
}

private val MediaCategory.title
    get() =
        when (this) {
            MediaCategory.CURRENT_SEASON_ANIME -> "Popular this season"
            MediaCategory.NEXT_SEASON_ANIME -> "Upcoming next season"
            MediaCategory.TRENDING_ANIME -> "Trending now"
            MediaCategory.MOVIE_ANIME -> "Movie"
            MediaCategory.TRENDING_MANGA -> "Popular this season"
            MediaCategory.ALL_TIME_POPULAR_MANGA -> "Popular this season"
            MediaCategory.TOP_MANHWA -> "Popular this season"
            MediaCategory.NEW_ADDED_ANIME -> "New Added Anime"
            MediaCategory.NEW_ADDED_MANGA -> "Popular this season"
        }
