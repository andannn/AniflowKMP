/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.relation

import me.andannn.aniflow.data.model.CharacterModel
import me.andannn.aniflow.data.model.MediaModel

data class VoicedCharacterWithMedia(
    val character: CharacterModel,
    val media: MediaModel,
)
