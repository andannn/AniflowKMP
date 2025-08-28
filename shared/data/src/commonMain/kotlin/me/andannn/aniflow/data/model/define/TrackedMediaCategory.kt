/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

import kotlinx.serialization.Serializable

@Serializable
enum class TrackedMediaCategory {
    ALL,
    NEW_AIRED,
    HAS_NEXT,
}
