/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import me.andannn.aniflow.data.model.define.MediaCategory
import org.koin.mp.KoinPlatform.getKoin

interface PlatformAnalytics {
    fun logEvent(
        event: String,
        params: Map<String, String> = emptyMap(),
    )

    fun recordException(throwable: Throwable)
}

fun FaEvent.logScreenEventEvent() {
    getKoin().get<PlatformAnalytics>().logEvent(
        event = eventName,
        params = params,
    )
}

sealed interface FaEvent {
    val eventName: String
    val params: Map<String, String>
}

sealed class ScreenEvent(
    override val params: Map<String, String>,
) : FaEvent {
    override val eventName: String = "screen_transition"

    data object Home : ScreenEvent(
        params =
            mapOf(
                SCREEN_DIMENSION to "home",
            ),
    )

    data class MediaCategoryList(
        val category: MediaCategory,
    ) : ScreenEvent(
            params =
                mapOf(
                    SCREEN_DIMENSION to "media_category_list",
                    "category" to category.name,
                ),
        )

    data object Notification : ScreenEvent(
        params =
            mapOf(
                SCREEN_DIMENSION to "notification",
            ),
    )

    data object Search : ScreenEvent(
        params =
            mapOf(
                SCREEN_DIMENSION to "search",
            ),
    )

    data object Settings : ScreenEvent(
        params =
            mapOf(
                SCREEN_DIMENSION to "settings",
            ),
    )

    data class DetailStaffPaging(
        val mediaId: String,
    ) : ScreenEvent(
            params =
                mapOf(
                    SCREEN_DIMENSION to "detail_staff_paging",
                    "media_id" to mediaId,
                ),
        )

    data class DetailStudioPaging(
        val studioId: String,
    ) : ScreenEvent(
            params =
                mapOf(
                    SCREEN_DIMENSION to "detail_studio_paging",
                    "studio_id" to studioId,
                ),
        )

    data class DetailCharacterPaging(
        val mediaId: String,
    ) : ScreenEvent(
            params =
                mapOf(
                    SCREEN_DIMENSION to "detail_character_paging",
                    "media_id" to mediaId,
                ),
        )

    data class DetailMedia(
        val mediaId: String,
    ) : ScreenEvent(
            params =
                mapOf(
                    SCREEN_DIMENSION to "detail_media",
                    "media_id" to mediaId,
                ),
        )

    data class DetailStaff(
        val staffId: String,
    ) : ScreenEvent(
            params =
                mapOf(
                    SCREEN_DIMENSION to "detail_staff",
                    "staff_id" to staffId,
                ),
        )

    data class DetailCharacter(
        val characterId: String,
    ) : ScreenEvent(
            params =
                mapOf(
                    SCREEN_DIMENSION to "detail_character",
                    "character_id" to characterId,
                ),
        )

    data object Login : ScreenEvent(
        params =
            mapOf(
                SCREEN_DIMENSION to "login",
            ),
    )

    data object PresentationDialog : ScreenEvent(
        params =
            mapOf(
                SCREEN_DIMENSION to "presentation_dialog",
            ),
    )

    data class ScoringDialog(
        val mediaId: String,
    ) : ScreenEvent(
            params =
                mapOf(
                    SCREEN_DIMENSION to "scoring_dialog",
                    "media_id" to mediaId,
                ),
        )

    data class TrackProgressDialog(
        val mediaId: String,
    ) : ScreenEvent(
            params =
                mapOf(
                    SCREEN_DIMENSION to "track_progress_dialog",
                    "media_id" to mediaId,
                ),
        )

    data class SettingOption(
        val setting: String,
        val selected: String,
    ) : ScreenEvent(
            params =
                mapOf(
                    SCREEN_DIMENSION to "setting_option",
                    "setting" to setting,
                    "selected" to selected,
                ),
        )

    companion object {
        private const val SCREEN_DIMENSION = "screen"
    }
}
