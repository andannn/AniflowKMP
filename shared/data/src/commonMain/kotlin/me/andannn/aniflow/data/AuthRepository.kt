/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow
import me.andannn.aniflow.data.model.UserModel
import me.andannn.aniflow.data.model.UserOptions

data class AuthToken(
    val token: String,
    val expiresInTime: Int,
)

interface BrowserAuthOperationHandler {
    /**
     * Wait for the authentication result.
     *
     * callBack will be invoked with the authentication result, Or null if the user cancelled the authentication.
     */
    fun getAuthResult(callBack: (AuthToken?) -> Unit)

    /**
     * Cancel the authentication process.
     */
    fun cancel()
}

interface AuthRepository {
    /**
     * Start the login process, which will open a browser for user authentication.
     * This function should be called when the user initiates the login process.
     *
     * @return returns an [AppError] if the login process fails, or null if it succeeds.
     */
    @NativeCoroutines()
    suspend fun startLoginProcessAndWaitResult(): AppError?

    @NativeCoroutines
    fun getAuthedUserFlow(): Flow<UserModel?>

    /**
     * Sync the authenticated user's data from the remote source.
     *
     * @return returns an [AppError] if the sync process fails, or null if it succeeds.
     */
    suspend fun syncUserCondition(): AppError?

    fun getUserOptionsFlow(): Flow<UserOptions>
}
