/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.andannn.aniflow.data.model.define.MediaCategory
import me.andannn.aniflow.usecase.data.provider.SettingItem

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

    @Serializable
    data object Settings : Screen

    @Serializable
    data class DetailStaffPaging(
        val mediaId: String,
    ) : Screen

    @Serializable
    data class DetailCharacterPaging(
        val mediaId: String,
    ) : Screen

    @Serializable
    data class DetailMedia(
        val mediaId: String,
    ) : Screen

    @Serializable
    data class DetailStaff(
        val staffId: String,
    ) : Screen

    @Serializable
    data class DetailCharacter(
        val characterId: String,
    ) : Screen

    @Serializable
    data class DetailStudio(
        val studioId: String,
    ) : Screen

    @Serializable
    data object MyList : Screen

    @Serializable
    sealed interface Dialog : Screen {
        @Serializable
        data object Login : Dialog

        @Serializable
        data class SettingOption(
            val settingItem: SettingItem,
        ) : Dialog

        @Serializable
        data class TrackProgressDialog(
            val mediaId: String,
        ) : Dialog

        @Serializable
        data class ScoringDialog(
            val mediaId: String,
        ) : Dialog

        @Serializable
        data object PresentationDialog : Dialog
    }

    fun toJson(): String = Json.encodeToString<Screen>(this)

    companion object {
        fun fromJson(json: String): Screen = Json.decodeFromString<Screen>(json)
    }
}

object DeepLinkHelper {
    const val NOTIFICATION_DOMAIN = "aniflow"

    fun parseUri(url: Uri?): Screen? {
        if (url == null) return null

        val host = url.host ?: return null
        val scheme = url.scheme ?: return null
        val path =
            url.path ?: return null

        if (scheme != NOTIFICATION_DOMAIN || host != "anilist.co") return null

        val segments = path.trim('/').split('/')
        if (segments.isEmpty()) return null

        return when (segments[0]) {
            "anime", "manga" -> {
                val mediaId = segments.getOrNull(1) ?: return null
                return Screen.DetailMedia(mediaId)
            }

            "character" -> {
                val characterId = segments.getOrNull(1) ?: return null
                return Screen.DetailCharacter(characterId)
            }

            "staff" -> {
                val staffId = segments.getOrNull(1) ?: return null
                return Screen.DetailStaff(staffId)
            }

            else -> {
                null
            }
        }
    }
}
