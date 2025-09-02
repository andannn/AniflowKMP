/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.model.Title
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import org.koin.compose.getKoin

@Composable
fun rememberUserTitle(
    title: Title,
    authRepository: AuthRepository = getKoin().get(),
): String {
    val option by authRepository.getUserOptionsFlow().collectAsStateWithLifecycle(null)
    return remember(title, option?.titleLanguage) {
        getUserTitle(title, option?.titleLanguage)
    }
}

fun getUserTitle(
    title: Title,
    titleLanguage: UserTitleLanguage?,
): String {
    titleLanguage ?: return ""
    return when (titleLanguage) {
        UserTitleLanguage.ROMAJI -> title.romaji ?: title.english ?: title.native ?: ""
        UserTitleLanguage.ENGLISH -> title.english ?: title.romaji ?: title.native ?: ""
        UserTitleLanguage.NATIVE -> title.native ?: title.romaji ?: title.english ?: ""
    }
}
