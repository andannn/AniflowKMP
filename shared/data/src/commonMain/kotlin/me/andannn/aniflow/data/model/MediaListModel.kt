/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

import me.andannn.aniflow.data.model.define.MediaListStatus

data class MediaListModel(
    val id: String,
    val status: MediaListStatus? = null,
    val score: Double? = null,
    val updatedAt: Int? = null,
    val progress: Int? = null,
    val progressVolumes: Int? = null,
    val startedAt: SimpleDate? = null,
    val completedAt: SimpleDate? = null,
    val notes: String? = null,
    val repeat: Int? = null,
    val isPrivate: Boolean = false,
)
