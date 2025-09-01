/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.widget

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.model.MediaListModel
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.Title
import me.andannn.aniflow.data.model.define.MediaFormat
import me.andannn.aniflow.data.model.define.MediaSeason
import me.andannn.aniflow.data.model.define.MediaSource
import me.andannn.aniflow.data.model.define.MediaStatus
import me.andannn.aniflow.data.model.define.MediaType
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem
import me.andannn.aniflow.ui.theme.AniflowTheme
import me.andannn.aniflow.ui.util.rememberUserTitle
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

private val OptionIconWidth = 128.dp

private enum class SwipeOptionStatus {
    NONE,
    LEFT_OPTION,
    RIGHT_OPTION,
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MediaRowItem(
    item: MediaWithMediaListItem,
    shape: Shape,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDelete: () -> Unit = {},
    onMarkWatched: () -> Unit = {},
    onLongPress: (() -> Unit)? = null,
    titleMaxLines: Int = 2,
) {
    val optionIconSizePx =
        with(LocalDensity.current) {
            OptionIconWidth.toPx()
        }
    val colorScheme = MaterialTheme.colorScheme
    val surfaceTextColor = colorScheme.onSurfaceVariant
    val textStyle = MaterialTheme.typography

    val state =
        remember {
            AnchoredDraggableState(
                initialValue = SwipeOptionStatus.NONE,
                anchors =
                    DraggableAnchors {
                        SwipeOptionStatus.NONE at 0f
                        SwipeOptionStatus.LEFT_OPTION at optionIconSizePx
                        SwipeOptionStatus.RIGHT_OPTION at -1 * optionIconSizePx
                    },
            )
        }

    val scope = rememberCoroutineScope()

    Box(
        modifier =
            modifier.height(IntrinsicSize.Min),
    ) {
        Surface(
            modifier =
                Modifier
                    .anchoredDraggable(
                        state = state,
                        orientation = Orientation.Horizontal,
                        flingBehavior =
                            AnchoredDraggableDefaults.flingBehavior(
                                state = state,
                            ),
                    ).graphicsLayer {
                        translationX = state.offset
                    },
            shape = shape,
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = onClick,
                            onLongClick = onLongPress,
                        ).padding(vertical = 8.dp, horizontal = 8.dp),
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
//                        val title = "Title"
                        val title = rememberUserTitle(item.mediaModel.title!!)
                        Text(
                            text = title,
                            style = textStyle.titleMedium.copy(color = surfaceTextColor),
                            maxLines = titleMaxLines,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(end = 4.dp),
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        val primaryColor = MaterialTheme.colorScheme.primary
                        val centerText =
                            remember(item) {
                                if (item.haveNextEpisode) {
                                    buildSpecialMessageText(
                                        "Next up: Episode ${(item.mediaListModel.progress ?: 0) + 1}",
                                        primaryColor,
                                    )
                                } else if (item.hasReleaseInfo) {
                                    val nextEpisode = item.mediaModel.nextAiringEpisode?.episode
                                    val durationUtilAir = item.mediaModel.releasingTimeString()
                                    buildSpecialMessageText(
                                        "Episode $nextEpisode in $durationUtilAir",
                                        primaryColor,
                                    )
                                } else {
                                    buildSpecialMessageText(
                                        "No upcoming episode",
                                        primaryColor,
                                    )
                                }
                            }

                        Text(text = centerText)

                        Spacer(modifier = Modifier.height(16.dp))

                        val info =
                            remember(item.mediaModel) {
                                item.mediaModel.infoString()
                            }
                        Text(
                            text = info,
                            style = textStyle.bodySmall.copy(color = surfaceTextColor),
                        )
                    }
                }
            }
        }

        if (state.offset > 0f) {
            val widthDp =
                with(LocalDensity.current) {
                    state.offset.absoluteValue.toDp()
                }
            Surface(
                modifier =
                    Modifier
                        .width(widthDp)
                        .fillMaxHeight(),
                onClick = {
                    scope.launch {
                        state.animateTo(SwipeOptionStatus.NONE)
                    }
                    onDelete()
                },
                shape = MaterialTheme.shapes.extraExtraLarge,
                color = MaterialTheme.colorScheme.error,
            ) {
                Icon(
                    modifier =
                        Modifier
                            .requiredSize(48.dp)
                            .align(Alignment.Center),
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                )
            }
        }

        if (state.offset < 0f) {
            val widthDp =
                with(LocalDensity.current) {
                    state.offset.absoluteValue.toDp()
                }
            Surface(
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .width(widthDp)
                        .fillMaxHeight(),
                onClick = {
                    scope.launch {
                        state.animateTo(SwipeOptionStatus.NONE)
                    }
                    onMarkWatched()
                },
                shape = MaterialTheme.shapes.extraExtraLarge,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Icon(
                    modifier =
                        Modifier
                            .requiredSize(48.dp)
                            .align(Alignment.Center),
                    imageVector = Icons.Outlined.Bookmark,
                    contentDescription = null,
                )
            }
        }
    }
}

private fun MediaModel.releasingTimeString(): String? {
    val timeUntilAiring = nextAiringEpisode?.timeUntilAiring
    if (nextAiringEpisode == null || timeUntilAiring == null) {
        return null
    }

    val airingTimeString = timeUntilAiring.seconds.formattedString()

    return airingTimeString
}

private fun MediaModel.infoString(): String {
    val itemList = mutableListOf<String>()

    if (format != null) {
        var extra = ""
        if (type == MediaType.ANIME && source != null) {
            extra = "(${source!!.label()})"
        }
        itemList.add("${format!!.label()}$extra")
    }

    if (seasonYear != null) {
        itemList.add("$seasonYear")
    }

    if (season != null) {
        itemList.add(season!!.label())
    }

    if (episodes != null &&
        episodes != 0 &&
        (
            format == MediaFormat.MANGA ||
                format == MediaFormat.TV ||
                format == MediaFormat.OVA ||
                format == MediaFormat.ONA
        )
    ) {
        itemList.add("$episodes Ep")
    }

    if (status != null) {
        itemList.add(status!!.label())
    }

    return itemList.joinToString(" · ").ifEmpty { "----" }
}

private fun MediaSource.label() =
    when (this) {
        MediaSource.ORIGINAL -> "Original"
        MediaSource.MANGA -> "Manga"
        MediaSource.LIGHT_NOVEL -> "Light novel"
        MediaSource.GAME -> "Game"
        MediaSource.OTHER -> "Other"
        MediaSource.VISUAL_NOVEL -> "Visual novel"
        MediaSource.VIDEO_GAME -> "Video game"
        MediaSource.NOVEL -> "Novel"
        MediaSource.DOUJINSHI -> "Doujinshi"
        MediaSource.ANIME -> "Anime"
        MediaSource.WEB_NOVEL -> "Web Novel"
        MediaSource.LIVE_ACTION -> "Live action"
        MediaSource.COMIC -> "Comic"
        MediaSource.MULTIMEDIA_PROJECT -> "Multimedia Project"
        MediaSource.PICTURE_BOOK -> "Picture book"
    }

private fun MediaFormat.label() =
    when (this) {
        MediaFormat.TV -> "TV"
        MediaFormat.TV_SHORT -> "TV Short"
        MediaFormat.MOVIE -> "Movie"
        MediaFormat.SPECIAL -> "Special"
        MediaFormat.OVA -> "OVA"
        MediaFormat.ONA -> "ONA"
        MediaFormat.MUSIC -> "Music"
        MediaFormat.MANGA -> "Manga"
        MediaFormat.NOVEL -> "Novel"
        MediaFormat.ONE_SHOT -> "One shot"
    }

private fun MediaStatus.label() =
    when (this) {
        MediaStatus.FINISHED -> "Finished"
        MediaStatus.RELEASING -> "Releasing"
        MediaStatus.NOT_YET_RELEASED -> "Not yet released"
        MediaStatus.CANCELLED -> "Cancelled"
        MediaStatus.HIATUS -> "Hiatus"
    }

private fun MediaSeason.label() =
    when (this) {
        MediaSeason.WINTER -> "Winter"
        MediaSeason.SPRING -> "Spring"
        MediaSeason.SUMMER -> "Summer"
        MediaSeason.FALL -> "Fall"
    }

@OptIn(ExperimentalTime::class)
@Composable
@Preview
private fun MediaListModelPreview() {
    AniflowTheme {
        MediaRowItem(
            shape = MaterialTheme.shapes.small,
            item =
                MediaWithMediaListItem(
                    mediaModel =
                        MediaModel(
                            id = "1",
                            title =
                                Title(
                                    romaji = "Shingeki no Kyojin",
                                    english = "Attack on Titan",
                                    native = "進撃の巨人",
                                ),
                            isFavourite = false,
                        ),
                    mediaListModel =
                        MediaListModel(
                            id = "1",
                        ),
                    airingScheduleUpdateTime = null,
                ),
            titleMaxLines = Int.MAX_VALUE,
            onClick = {},
        )
    }
}
