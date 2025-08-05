/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.components.track

import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem

interface TrackComponent : BackHandlerOwner {
    val content: Value<Content>

    data class Content(
        val items: List<MediaWithMediaListItem>,
    )
}

enum class TrackListFilter {
    ALL,
    NEW_AIRED,
    HAS_NEXT,
}
