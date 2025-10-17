/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

import kotlinx.serialization.Serializable
import me.andannn.aniflow.data.internal.AnimeSeasonParam
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem

@Serializable
enum class MediaListSort(
    override val key: String,
) : StringKeyEnum {
    START_DATE_DESC("START_DATE_DESC"),
}

fun Iterable<MediaWithMediaListItem>.sorted(sort: MediaListSort) =
    when (sort) {
        MediaListSort.START_DATE_DESC ->
            this.sortedByDescending { item ->
                AnimeSeasonParam(
                    item.mediaModel.seasonYear ?: 0,
                    item.mediaModel.season ?: MediaSeason.WINTER,
                )
            }
    }
