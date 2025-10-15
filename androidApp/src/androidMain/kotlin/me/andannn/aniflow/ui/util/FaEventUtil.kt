/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.util

import me.andannn.aniflow.data.FaEvent
import me.andannn.aniflow.data.ScreenEvent
import me.andannn.aniflow.data.model.SettingItem
import me.andannn.aniflow.ui.Screen

fun Screen.toFaEvent() =
    when (this) {
        is Screen.DetailCharacter -> ScreenEvent.DetailCharacter(characterId)
        is Screen.DetailCharacterPaging -> ScreenEvent.DetailCharacterPaging(mediaId)
        is Screen.DetailMedia -> ScreenEvent.DetailMedia(mediaId)
        is Screen.DetailStaff -> ScreenEvent.DetailStaff(staffId)
        is Screen.DetailStaffPaging -> ScreenEvent.DetailStaffPaging(mediaId)
        is Screen.DetailStudio -> ScreenEvent.DetailStudioPaging(studioId)
        Screen.Home -> ScreenEvent.Home
        is Screen.MediaCategoryList -> ScreenEvent.MediaCategoryList(category)
        Screen.Notification -> ScreenEvent.Notification
        Screen.Search -> ScreenEvent.Search
        Screen.Settings -> ScreenEvent.Settings
        Screen.Dialog.Login -> ScreenEvent.Login
        Screen.Dialog.PresentationDialog -> ScreenEvent.PresentationDialog
        is Screen.Dialog.ScoringDialog -> ScreenEvent.ScoringDialog(mediaId)
        is Screen.Dialog.SettingOption ->
            when (settingItem) {
                is SettingItem.SingleSelect -> {
                    ScreenEvent.SettingOption(settingItem.title, settingItem.selectedOption.label)
                }
            }

        is Screen.Dialog.TrackProgressDialog -> ScreenEvent.TrackProgressDialog(mediaId = mediaId)
    }
