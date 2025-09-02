/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.util

import me.andannn.aniflow.data.model.Title
import me.andannn.aniflow.data.model.define.UserTitleLanguage

fun Title?.getUserTitleString(titleLanguage: UserTitleLanguage?): String {
    titleLanguage ?: return ""
    val title = this ?: return ""
    return when (titleLanguage) {
        UserTitleLanguage.ROMAJI -> title.romaji ?: title.english ?: title.native ?: ""
        UserTitleLanguage.ENGLISH -> title.english ?: title.romaji ?: title.native ?: ""
        UserTitleLanguage.NATIVE -> title.native ?: title.romaji ?: title.english ?: ""
    }
}
