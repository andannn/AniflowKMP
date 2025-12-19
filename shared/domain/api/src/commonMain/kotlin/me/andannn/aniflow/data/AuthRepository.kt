/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow
import me.andannn.aniflow.data.model.UserModel
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.data.model.define.ScoreFormat
import me.andannn.aniflow.data.model.define.Theme
import me.andannn.aniflow.data.model.define.UserStaffNameLanguage
import me.andannn.aniflow.data.model.define.UserTitleLanguage

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
     * Cancel the authentication process by call site.
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
    @NativeCoroutines
    suspend fun startLoginProcessAndWaitResult(): AppError?

    /**
     * Get a flow of the authenticated user's data.
     * The flow will emit null if the user is not authenticated.
     */
    @NativeCoroutines
    fun getAuthedUserFlow(): Flow<UserModel?>

    /**
     * Sync the authenticated user's data from the remote source.
     *
     * @return returns an [AppError] if the sync process fails, or null if it succeeds.
     */
    suspend fun syncUserCondition(): AppError?

    /**
     * Get a flow of the authenticated user's options.
     */
    @NativeCoroutines
    fun getUserOptionsFlow(): Flow<UserOptions>

    /**
     * Logout the authenticated user.
     */
    @NativeCoroutines
    suspend fun logout()

    /**
     * Update the user's settings.
     *
     * @return returns an [AppError] if the update process fails, or null if it succeeds.
     */
    @NativeCoroutines
    suspend fun updateUserSettings(
        titleLanguage: UserTitleLanguage? = null,
        staffCharacterNameLanguage: UserStaffNameLanguage? = null,
        appTheme: Theme? = null,
        scoreFormat: ScoreFormat? = null,
    ): AppError?
}
