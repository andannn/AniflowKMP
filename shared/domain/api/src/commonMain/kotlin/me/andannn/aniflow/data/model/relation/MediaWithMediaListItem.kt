/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.relation

import me.andannn.aniflow.data.model.MediaListModel
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.define.MediaStatus
import me.andannn.aniflow.data.model.define.MediaType
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class MediaWithMediaListItem
    @OptIn(ExperimentalTime::class)
    constructor(
        val mediaModel: MediaModel,
        val mediaListModel: MediaListModel,
        val airingScheduleUpdateTime: Instant?,
        val firstAddedTime: Instant?,
    ) {
        constructor(
            mediaModel: MediaModel,
            mediaListModel: MediaListModel,
        ) : this(
            mediaModel = mediaModel,
            mediaListModel = mediaListModel,
            airingScheduleUpdateTime = null,
            firstAddedTime = null,
        )

        val haveNextEpisode: Boolean by lazy {
            haveNextEpisode(mediaModel, mediaListModel)
        }

        /**
         * 是否为最近更新（有下一集且三天内更新过）
         */
        @OptIn(ExperimentalTime::class)
        val isNewReleased: Boolean =
            haveNextEpisode && (
                airingScheduleUpdateTime != null &&
                    !isElapsed(
                        NEW_RELEASED_DAYS_THRESHOLD,
                        airingScheduleUpdateTime,
                    )
            )

        val hasReleaseInfo =
            mediaModel.nextAiringEpisode != null && mediaModel.nextAiringEpisode.timeUntilAiring != null &&
                mediaModel.nextAiringEpisode.episode != null

        companion object {
            const val NEW_RELEASED_DAYS_THRESHOLD = 3
        }
    }

@OptIn(ExperimentalTime::class)
internal fun isElapsed(
    day: Int,
    airingScheduleUpdateTime: Instant,
): Boolean {
    val now = Clock.System.now()
    val elapsed = now - airingScheduleUpdateTime
    return elapsed.inWholeDays >= day
}

private fun haveNextEpisode(
    mediaModel: MediaModel,
    mediaListModel: MediaListModel,
): Boolean =
    hasNextReleasingEpisode(
        type = mediaModel.type,
        status = mediaModel.status ?: MediaStatus.FINISHED,
        progress = mediaListModel.progress ?: 0,
        nextAiringEpisode = mediaModel.nextAiringEpisode?.episode,
        episodes = mediaModel.episodes,
    )

private fun hasNextReleasingEpisode(
    type: MediaType?,
    status: MediaStatus?,
    nextAiringEpisode: Int?,
    progress: Int,
    episodes: Int?,
): Boolean {
    // manga 没有 nextAiringEpisode，视为总有下一话
    if (type == MediaType.MANGA) return true

    return when (status) {
        MediaStatus.CANCELLED,
        MediaStatus.HIATUS,
        MediaStatus.RELEASING,
        -> {
            if (nextAiringEpisode == null) {
                // 服务器缺 nextAiringEpisode 但状态仍是 releasing
                progress < (episodes ?: 1)
            } else {
                // 已看进度比“下一集-1”小，说明还有下一集可看
                progress < (nextAiringEpisode - 1)
            }
        }

        MediaStatus.FINISHED -> {
            progress < (episodes ?: 1)
        }

        MediaStatus.NOT_YET_RELEASED,
        null,
        -> {
            false
        }
    }
}
