/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

/**
 * The language the user wants to see media titles in
 */
enum class UserTitleLanguage(
    override val key: String,
) : StringKeyEnum {
    /**
     * The romanization of the native language title
     */
    ROMAJI("ROMAJI"),

    /**
     * The official english title
     */
    ENGLISH("ENGLISH"),

    /**
     * Official title in it's native language
     */
    NATIVE("NATIVE"),

    ;

    companion object {
        val Default = NATIVE
    }
}
