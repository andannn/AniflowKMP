/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

import kotlinx.serialization.Serializable
import me.andannn.aniflow.data.model.define.UserStaffNameLanguage
import me.andannn.aniflow.data.model.define.UserTitleLanguage

@Serializable
public data class UserOptions(
    /**
     * The language the user wants to see media titles in
     */
    public val titleLanguage: UserTitleLanguage = UserTitleLanguage.Default,
    /**
     * The language the user wants to see staff and character names in
     */
    public val staffNameLanguage: UserStaffNameLanguage = UserStaffNameLanguage.Default,
) {
    companion object {
        val Default = UserOptions()
    }
}
