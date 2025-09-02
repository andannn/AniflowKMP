/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SavedMediaListResponse(
    @SerialName(value = "SaveMediaListEntry")
    public val mediaList: MediaList,
)
