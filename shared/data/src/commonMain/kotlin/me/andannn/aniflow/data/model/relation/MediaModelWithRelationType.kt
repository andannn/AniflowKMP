/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.relation

import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.define.MediaRelation

data class MediaModelWithRelationType(
    val media: MediaModel,
    val relationType: MediaRelation,
)
