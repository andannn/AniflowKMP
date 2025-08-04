/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import kotlinx.coroutines.flow.Flow
import me.andannn.aniflow.data.model.UserModel

data class AuthToken(
    val token: String,
    val expiresInTime: Int,
)

interface BrowserAuthOperationHandler {
    fun openBrowser()

    suspend fun awaitAuthResult(): AuthToken
}

interface AuthRepository {
    /**
     * Start the login process, which will open a browser for user authentication.
     * This function should be called when the user initiates the login process.
     *
     * @throws me.andannn.aniflow.data.exceptions.RemoteApiException server error
     */
    suspend fun startLoginProcessAndWaitResult()

    fun getAuthedUser(): Flow<UserModel?>
}
