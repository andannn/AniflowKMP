/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

enum class ExternalLinkType(
    override val key: String,
) : StringKeyEnum {
    INFO("INFO"),
    STREAMING("STREAMING"),
    SOCIAL("SOCIAL"),
}
