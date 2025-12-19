/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

import me.andannn.aniflow.data.model.define.ScoreFormat
import me.andannn.aniflow.data.model.define.Theme
import me.andannn.aniflow.data.model.define.UserStaffNameLanguage
import me.andannn.aniflow.data.model.define.UserTitleLanguage

public data class UserOptions constructor(
    /**
     * The language the user wants to see media titles in
     */
    public val titleLanguage: UserTitleLanguage = UserTitleLanguage.Default,
    /**
     * The language the user wants to see staff and character names in
     */
    public val staffNameLanguage: UserStaffNameLanguage = UserStaffNameLanguage.Default,
    /**
     * Whether the user wants to see adult content
     */
    val appTheme: Theme = Theme.SYSTEM,
    val scoreFormat: ScoreFormat = ScoreFormat.POINT_10,
) {
    companion object {
        val Default = UserOptions()
    }
}
