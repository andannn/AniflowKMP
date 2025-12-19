/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.relation

import me.andannn.aniflow.data.model.CharacterModel
import me.andannn.aniflow.data.model.StaffModel
import me.andannn.aniflow.data.model.define.CharacterRole
import me.andannn.aniflow.data.model.define.StaffLanguage

data class CharacterWithVoiceActor(
    val character: CharacterModel,
    val voiceActor: StaffModel?,
    val role: CharacterRole?,
    val voiceActorLanguage: StaffLanguage,
)
