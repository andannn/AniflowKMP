/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.ScoreFormat

data class MediaListModel(
    val id: String,
    val status: MediaListStatus,
    val score: Double? = null,
    val updatedAt: Int? = null,
    val progress: Int? = null,
    val progressVolumes: Int? = null,
    val startedAt: SimpleDate? = null,
    val completedAt: SimpleDate? = null,
    val notes: String? = null,
    val repeat: Int? = null,
    val isPrivate: Boolean = false,
) {
    fun scoreLabel(scoreFormat: ScoreFormat) =
        score?.takeIf { it > 0f }?.let { score ->
            when (scoreFormat) {
                ScoreFormat.POINT_100 -> "$score/100"
                ScoreFormat.POINT_10 -> "$score/10"
                ScoreFormat.POINT_10_DECIMAL -> "$score/10.0"
                ScoreFormat.POINT_5 -> "$score/5"
                ScoreFormat.POINT_3 -> "$score/3"
            }
        }

    fun progressLabel() =
        progress
            ?.takeIf {
                it > 0
            }?.let { "ep.$it" }
}
