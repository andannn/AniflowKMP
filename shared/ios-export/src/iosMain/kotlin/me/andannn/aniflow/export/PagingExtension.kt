/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.export

import me.andannn.aniflow.data.AppErrorHandler
import me.andannn.aniflow.data.model.define.MediaCategory
import me.andannn.aniflow.data.model.define.MediaSort
import me.andannn.aniflow.data.model.define.NotificationCategory
import me.andannn.aniflow.data.model.define.StaffLanguage
import me.andannn.aniflow.usecase.data.paging.CharacterDetailMediaPaging
import me.andannn.aniflow.usecase.data.paging.DetailMediaCharacterPageComponent
import me.andannn.aniflow.usecase.data.paging.DetailMediaStaffPageComponent
import me.andannn.aniflow.usecase.data.paging.MediaCategoryPageComponent
import me.andannn.aniflow.usecase.data.paging.NotificationPageComponent
import me.andannn.aniflow.usecase.data.paging.StaffCharactersPageComponent

object PagingExtension {
    // interop with swift.
    fun createMediaCategoryPageComponent(
        category: MediaCategory,
        errorHandler: AppErrorHandler,
    ): MediaCategoryPageComponent = MediaCategoryPageComponent(category, errorHandler = errorHandler)

    // interop with swift.
    fun createNotificationPageComponent(
        category: NotificationCategory,
        errorHandler: AppErrorHandler,
    ): NotificationPageComponent = NotificationPageComponent(category, errorHandler = errorHandler)

    // interop with swift.
    fun createDetailMediaStaffPaging(mediaId: String): DetailMediaStaffPageComponent = DetailMediaStaffPageComponent(mediaId)

    // interop with swift.
    fun createDetailMediaCharacterPaging(mediaId: String): DetailMediaCharacterPageComponent =
        DetailMediaCharacterPageComponent(
            mediaId,
            StaffLanguage.JAPANESE,
        )

    // interop with swift.
    fun createStaffCharactersPaging(
        staffId: String,
        sort: MediaSort,
    ): StaffCharactersPageComponent =
        StaffCharactersPageComponent(
            staffId,
            sort,
        )

    // interop with swift.
    fun characterDetailMediaPaging(
        characterId: String,
        sort: MediaSort,
    ): CharacterDetailMediaPaging =
        CharacterDetailMediaPaging(
            characterId,
            sort,
        )
}
