/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.token

import me.andannn.aniflow.service.TokenProvider

class DataStoreTokenProvider : TokenProvider {
    override suspend fun getAccessToken(): String? {
// TODO: Implement DataStore access token retrieval
        return "aa"
    }
}
