/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal.dataprovider

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.SettingUiDataProvider
import me.andannn.aniflow.data.SyncStatus
import me.andannn.aniflow.data.internal.tasks.SyncUserConditionTask
import me.andannn.aniflow.data.internal.tasks.createSideEffectFlow
import me.andannn.aniflow.data.model.SettingGroup
import me.andannn.aniflow.data.model.SettingItem
import me.andannn.aniflow.data.model.SettingOption
import me.andannn.aniflow.data.model.SettingUiState
import me.andannn.aniflow.data.model.UserModel
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.data.model.define.ScoreFormat
import me.andannn.aniflow.data.model.define.Theme
import me.andannn.aniflow.data.model.define.UserStaffNameLanguage
import me.andannn.aniflow.data.model.define.UserTitleLanguage

internal class SettingDataProviderImpl(
    private val authRepository: AuthRepository,
) : SettingUiDataProvider {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun uiDataFlow(): Flow<SettingUiState> {
        val settingGroupListFlow =
            authRepository
                .getAuthedUserFlow()
                .distinctUntilChanged()
                .flatMapLatest { authedUser ->
                    with(authRepository) {
                        settingGroupsFlow(authedUser)
                    }
                }

        return settingGroupListFlow.map { settingGroupList ->
            SettingUiState(
                settingGroupList = settingGroupList,
            )
        }
    }

    override fun uiSideEffect(forceRefreshFirstTime: Boolean): Flow<SyncStatus> =
        createSideEffectFlow(
            forceRefreshFirstTime,
            SyncUserConditionTask(),
        )
}

context(authRepository: AuthRepository)
private fun settingGroupsFlow(authedUser: UserModel?): Flow<List<SettingGroup>> =
    authRepository.getUserOptionsFlow().map {
        buildSettingGroup(
            authed = authedUser != null,
            userOptions = it,
        )
    }

private fun buildSettingGroup(
    authed: Boolean,
    userOptions: UserOptions,
): List<SettingGroup> {
    val selectedTitleLanguage = userOptions.titleLanguage
    val selectedStaffNameLanguage = userOptions.staffNameLanguage
    val selectedTheme = userOptions.appTheme
    val selectedScoreFormat = userOptions.scoreFormat

    fun titleLanguageSetting() =
        SettingItem.SingleSelect.build(
            title = "Title Language",
            buildOptions = {
                addAll(
                    UserTitleLanguage.entries.map { it.option },
                )
            },
            selectedOption = selectedTitleLanguage.option,
        )

    fun staffNameLanguageSetting() =
        SettingItem.SingleSelect.build(
            title = "Staff Name Language",
            buildOptions = {
                addAll(
                    UserStaffNameLanguage.entries.map { it.option },
                )
            },
            selectedOption = selectedStaffNameLanguage.option,
        )

    fun scoreFormatSetting() =
        SettingItem.SingleSelect.build(
            title = "Score Format",
            buildOptions = {
                addAll(
                    ScoreFormat.entries.map { it.option },
                )
            },
            selectedOption = selectedScoreFormat.option,
        )

    fun appSettingGroup() =
        SettingGroup.build(
            title = "App Settings",
            settingItemList = {
                add(
                    SettingItem.SingleSelect.build(
                        title = "Theme",
                        buildOptions = {
                            addAll(
                                Theme.entries.map { it.option },
                            )
                        },
                        selectedOption = selectedTheme.option,
                    ),
                )
            },
        )

    fun mediaRelatedSettingGroup() =
        SettingGroup.build(
            title = "Anime & Manga",
            settingItemList = {
                add(titleLanguageSetting())
                add(staffNameLanguageSetting())
                add(scoreFormatSetting())
            },
        )

    return buildList {
        add(appSettingGroup())

        if (authed) {
            add(mediaRelatedSettingGroup())
        }
    }
}

private val UserTitleLanguage.option: SettingOption
    get() = SettingOption.UserTitleLanguageOption(this)

private val UserStaffNameLanguage.option: SettingOption
    get() = SettingOption.StaffCharacterNameOption(this)

private val Theme.option: SettingOption
    get() = SettingOption.ThemeModeOption(this)

private val ScoreFormat.option: SettingOption
    get() = SettingOption.ScoreFormatOption(this)
