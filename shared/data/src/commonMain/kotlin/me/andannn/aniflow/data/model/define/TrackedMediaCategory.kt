/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

import kotlinx.serialization.Serializable

@Serializable
enum class TrackedMediaCategory(
    override val key: String,
) : StringKeyEnum {
    ALL("ALL"),
    NEW_AIRED("NEW_AIRED"),
    HAS_NEXT("HAS_NEXT"),
}
