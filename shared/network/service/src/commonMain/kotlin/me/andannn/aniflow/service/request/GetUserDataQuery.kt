/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.UpdateUserRespond
import me.andannn.network.common.schemas.USER_DATA_MUTATION_SCHEMA

@Serializable
internal data object GetUserDataQuery : GraphQLQuery<DataWrapper<UpdateUserRespond>> {
    override fun getSchema() = USER_DATA_MUTATION_SCHEMA
}
