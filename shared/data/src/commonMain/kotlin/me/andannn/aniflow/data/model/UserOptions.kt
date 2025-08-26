package me.andannn.aniflow.data.model

import kotlinx.serialization.Serializable
import me.andannn.aniflow.data.model.define.UserStaffNameLanguage
import me.andannn.aniflow.data.model.define.UserTitleLanguage

@Serializable
public data class UserOptions(
    /**
     * The language the user wants to see media titles in
     */
    public val titleLanguage: UserTitleLanguage,
    /**
     * The language the user wants to see staff and character names in
     */
    public val staffNameLanguage: UserStaffNameLanguage,
)
