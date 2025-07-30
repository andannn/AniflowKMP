/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.UpdatedUserSettingResponse
import me.andannn.aniflow.service.dto.enums.ScoreFormat
import me.andannn.aniflow.service.dto.enums.UserStaffNameLanguage
import me.andannn.aniflow.service.dto.enums.UserTitleLanguage
import me.andannn.network.common.schemas.UPDATE_USER_SETTING_MUTATION_SCHEMA

@Serializable
internal data class UpdateUserSettingMutation(
    val titleLanguage: UserTitleLanguage?,
    val displayAdultContent: Boolean?,
    val userStaffNameLanguage: UserStaffNameLanguage?,
    val scoreFormat: ScoreFormat?,
) : GraphQLQuery<DataWrapper<UpdatedUserSettingResponse>> {
    override fun getSchema() = UPDATE_USER_SETTING_MUTATION_SCHEMA
}
