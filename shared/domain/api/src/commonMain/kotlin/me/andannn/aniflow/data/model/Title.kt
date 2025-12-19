/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

import me.andannn.aniflow.data.model.define.UserTitleLanguage

public data class Title(
    /**
     * The romanization of the native language title
     */
    public val romaji: String? = null,
    /**
     * The official english title
     */
    public val english: String? = null,
    /**
     * Official title in it's native language
     */
    public val native: String? = null,
)

fun Title?.getUserTitleString(titleLanguage: UserTitleLanguage?): String {
    titleLanguage ?: return ""
    val title = this ?: return ""
    return when (titleLanguage) {
        UserTitleLanguage.ROMAJI -> title.romaji ?: title.english ?: title.native ?: ""
        UserTitleLanguage.ENGLISH -> title.english ?: title.romaji ?: title.native ?: ""
        UserTitleLanguage.NATIVE -> title.native ?: title.romaji ?: title.english ?: ""
    }
}
