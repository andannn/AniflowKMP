/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.provider

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import me.andannn.aniflow.data.model.define.ScoreFormat
import me.andannn.aniflow.data.model.define.Theme
import me.andannn.aniflow.data.model.define.UserStaffNameLanguage
import me.andannn.aniflow.data.model.define.UserTitleLanguage

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

    @Serializable
    data class ScoreFormatOption(
        val value: ScoreFormat,
    ) : SettingOption {
        override val label: String
            get() =
                when (value) {
                    ScoreFormat.POINT_100 -> "100 Point (55/100)"
                    ScoreFormat.POINT_10 -> "10 Point (5/10)"
                    ScoreFormat.POINT_10_DECIMAL -> "10 Point Decimal (5.5/10)"
                    ScoreFormat.POINT_5 -> "5 Start (3/5)"
                    ScoreFormat.POINT_3 -> "3 Point Smiley :)"
                }
    }
}

data class SettingUiState(
    val settingGroupList: List<SettingGroup> = emptyList(),
) {
    companion object {
        val Empty = SettingUiState()
    }
}

interface SettingUiDataProvider : DataProvider {
    @NativeCoroutines
    fun uiDataFlow(): Flow<SettingUiState>
}
