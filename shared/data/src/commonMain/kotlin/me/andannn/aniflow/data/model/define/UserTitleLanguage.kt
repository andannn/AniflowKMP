/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

import kotlinx.serialization.Serializable

/**
 * The language the user wants to see media titles in
 */
@Serializable
enum class UserTitleLanguage {
    /**
     * The romanization of the native language title
     */
    ROMAJI,

    /**
     * The official english title
     */
    ENGLISH,

    /**
     * Official title in it's native language
     */
    NATIVE,
}
