/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

import kotlinx.serialization.Serializable
import me.andannn.aniflow.data.getUserTitleString
import me.andannn.aniflow.data.model.define.MediaContentMode
import me.andannn.aniflow.data.model.define.Theme
import me.andannn.aniflow.data.model.define.UserStaffNameLanguage
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import me.andannn.aniflow.data.model.relation.CategoryDataModel
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem

data class DiscoverUiState(
    val categoryDataMap: CategoryDataModel = CategoryDataModel(),
    val newReleasedMedia: List<MediaWithMediaListItem> = emptyList(),
    val userOptions: UserOptions = UserOptions(),
) {
    companion object {
        val Empty = DiscoverUiState()
    }
}

data class TrackUiState(
    private val items: List<MediaWithMediaListItem> = emptyList(),
    val userOptions: UserOptions = UserOptions(),
) {
    companion object {
        val Empty = TrackUiState()
    }

    data class CategoryWithItems(
        val category: TrackCategory,
        val items: List<MediaWithMediaListItem>,
    )

    val categoryWithItems: List<CategoryWithItems>

    init {
        val newItems = mutableListOf<MediaWithMediaListItem>()

        val nextItems = mutableListOf<MediaWithMediaListItem>()

        val upcomingItems = mutableListOf<MediaWithMediaListItem>()

        val otherItems = mutableListOf<MediaWithMediaListItem>()

        items.forEach {
            if (it.isNewReleased) {
                newItems.add(it)
            } else if (it.haveNextEpisode) {
                nextItems.add(it)
            } else if (it.hasReleaseInfo) {
                upcomingItems.add(it)
            } else {
                otherItems.add(it)
            }
        }

        categoryWithItems =
            listOf(
                CategoryWithItems(TrackCategory.NEW_RELEASED, newItems),
                CategoryWithItems(TrackCategory.UPCOMING, upcomingItems),
                CategoryWithItems(TrackCategory.NEXT_UP, nextItems),
                CategoryWithItems(TrackCategory.OTHER, otherItems),
            )
    }

    enum class TrackCategory(
        val title: String,
    ) {
        /**
         * 最近更新（有下一集且三天内更新过）
         */
        NEW_RELEASED("New Released"),

        /**
         * 下一集（有下一集但三天内没有更新过）
         */
        NEXT_UP("Next Up"),

        /**
         * 没有下一集， 有下一集的Release时间
         */
        UPCOMING("Upcoming"),

        /**
         * 其他
         */
        OTHER("Other"),
    }
}

data class HomeAppBarUiState(
    val authedUser: UserModel? = null,
    val contentMode: MediaContentMode = MediaContentMode.ANIME,
) {
    companion object {
        val Empty = HomeAppBarUiState()
    }
}

data class SettingGroup(
    val title: String,
    val settings: List<SettingItem>,
) {
    companion object {
        fun build(
            title: String,
            settingItemList: MutableList<SettingItem>.() -> Unit,
        ) = SettingGroup(
            title = title,
            settings = buildList(settingItemList),
        )
    }
}

@Serializable
sealed interface SettingOption {
    val label: String

    @Serializable
    data class UserTitleLanguageOption(
        val value: UserTitleLanguage,
    ) : SettingOption {
        override val label: String
            get() =
                when (value) {
                    UserTitleLanguage.NATIVE -> "Native"
                    UserTitleLanguage.ROMAJI -> "Romaji"
                    UserTitleLanguage.ENGLISH -> "English"
                }
    }

    @Serializable
    data class StaffCharacterNameOption(
        val value: UserStaffNameLanguage,
    ) : SettingOption {
        override val label: String
            get() =
                when (value) {
                    UserStaffNameLanguage.NATIVE -> "Native"
                    UserStaffNameLanguage.ROMAJI -> "Romaji"
                    UserStaffNameLanguage.ROMAJI_WESTERN -> "Romaji, Western Order"
                }
    }

    @Serializable
    data class ThemeModeOption(
        val value: Theme,
    ) : SettingOption {
        override val label: String
            get() =
                when (value) {
                    Theme.LIGHT -> "Light"
                    Theme.DARK -> "Dark"
                    Theme.SYSTEM -> "System"
                }
    }
}

@Serializable
sealed interface SettingItem {
    val title: String
    val subtitle: String?

    @Serializable
    data class SingleSelect(
        override val title: String,
        override val subtitle: String?,
        val options: List<SettingOption>,
        val selectedOption: SettingOption,
    ) : SettingItem {
        companion object {
            fun build(
                title: String,
                subtitle: String? = null,
                selectedOption: SettingOption,
                buildOptions: MutableList<SettingOption>.() -> Unit,
            ) = SingleSelect(
                title = title,
                subtitle = subtitle,
                selectedOption = selectedOption,
                options = buildList(buildOptions),
            )
        }
    }
}

data class SettingUiState(
    val settingGroupList: List<SettingGroup> = emptyList(),
)

data class DetailUiState(
    val mediaModel: MediaModel?,
    val mediaListItem: MediaListModel? = null,
    val studioList: List<StudioModel> = emptyList(),
    val staffList: List<StaffWithRole> = emptyList(),
    val userOptions: UserOptions = UserOptions.Default,
    val authedUser: UserModel? = null,
) {
    val title: String
        get() = mediaModel?.title?.getUserTitleString(userOptions.titleLanguage) ?: ""

    companion object {
        val Empty =
            DetailUiState(
                mediaModel = null,
            )
    }
}
