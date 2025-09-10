/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import me.andannn.aniflow.data.model.define.MediaCategory

@Serializable
sealed interface Screen : NavKey {
    @Serializable
    data object Home : Screen

    @Serializable
    data class MediaCategoryList(
        val category: MediaCategory,
    ) : Screen

    @Serializable
    data object Notification : Screen

    @Serializable
    data object Search : Screen

    sealed interface Dialog : Screen {
        @Serializable
        data object Login : Dialog
    }
}

object DeepLinkHelper {
    const val NOTIFICATION_DOMAIN = "aniflow"

    fun parseUri(url: String): Screen? {
//        val u = runCatching { Url(url) }.getOrNull() ?: return null
//
//        val host = u.host.lowercase()
//        val scheme = u.protocol.name.lowercase()
//        val segments =
//            u.encodedPath
//                .trim('/')
//                .split('/')
//                .filter { it.isNotEmpty() }
//
//        if (scheme != NOTIFICATION_DOMAIN || host != "anilist.co") return null
//
        // TODO: Handle deep links
        return null
    }
}
