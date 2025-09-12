/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service

interface TokenProvider {
    /**
     * Get the access token.
     *
     * @return The access token, or null if not available.
     */
    suspend fun getAccessToken(): String?
}

internal class DummyTokenProvider : TokenProvider {
    override suspend fun getAccessToken(): String? = "DummyAccessToken"
}
