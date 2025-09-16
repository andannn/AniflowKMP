/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import me.andannn.aniflow.data.AppError
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.BrowserAuthOperationHandler
import me.andannn.aniflow.data.internal.exceptions.toError
import me.andannn.aniflow.data.internal.util.UserSettingSyncer
import me.andannn.aniflow.data.internal.util.postMutationAndRevertWhenException
import me.andannn.aniflow.data.model.UserModel
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.data.model.define.Theme
import me.andannn.aniflow.data.model.define.UserStaffNameLanguage
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import me.andannn.aniflow.data.model.define.deserialize
import me.andannn.aniflow.database.MediaLibraryDao
import me.andannn.aniflow.database.schema.UserEntity
import me.andannn.aniflow.datastore.UserSettingPreferences
import me.andannn.aniflow.service.AniListService
import me.andannn.aniflow.service.ServerException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val TAG = "AuthRepository"

internal class AuthRepositoryImpl(
    private val service: AniListService,
    private val userPref: UserSettingPreferences,
    private val database: MediaLibraryDao,
    private val authHandler: BrowserAuthOperationHandler,
) : AuthRepository {
    @OptIn(ExperimentalTime::class)
    override suspend fun startLoginProcessAndWaitResult(): AppError? =
        withContext(Dispatchers.Main) {
            val authResult = authHandler.awaitAuthResult()

            Napier.d(tag = TAG) { "token received: ${authResult.token}, expires in: ${authResult.expiresInTime} seconds" }
            userPref.setAuthToken(
                token = authResult.token,
                expiredTime = Clock.System.now().epochSeconds + authResult.expiresInTime,
            )

            try {
                // retrieve user data from ani list api
                val authedUser = service.getAuthedUserData()
                userPref.setAuthedUserId(authedUser.id.toString())
                database.upsertUser(listOf(authedUser.toEntity()))
                null
            } catch (exception: ServerException) {
                exception.toError()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAuthedUserFlow(): Flow<UserModel?> =
        userPref.userData
            .map { it.authedUserId }
            .distinctUntilChanged()
            .flatMapLatest { authedUserId ->
                if (authedUserId == null) {
                    // if the user is not authenticated, return null
                    flowOf(null)
                } else {
                    database.getUserFlow(authedUserId).map(UserEntity::toDomain)
                }
            }

    override suspend fun syncUserCondition(): AppError? =
        try {
            val user = service.getAuthedUserData()
            database.upsertUser(listOf(user.toEntity()))
            val options = user.options ?: error("User options is null")
            options.titleLanguage?.toDomainType()?.let {
                userPref.setTitleLanguage(it.key)
            }
            options.staffNameLanguage?.toDomainType()?.let {
                userPref.setStaffNameLanguage(it.key)
            }
// TODO: save options
            null
        } catch (exception: ServerException) {
            exception.toError()
        }

    override fun getUserOptionsFlow(): Flow<UserOptions> =
        userPref.userData
            .map {
                UserOptions(
                    titleLanguage =
                        it.titleLanguage?.deserialize()
                            ?: UserTitleLanguage.Default,
                    staffNameLanguage =
                        it.staffNameLanguage?.deserialize()
                            ?: UserStaffNameLanguage.Default,
                    appTheme =
                        it.appTheme?.deserialize() ?: Theme.SYSTEM,
                )
            }.distinctUntilChanged()

    override suspend fun logout() {
        userPref.setAuthedUserId(null)
        userPref.clearAuthToken()
    }

    override suspend fun updateUserSettings(
        titleLanguage: UserTitleLanguage?,
        staffCharacterNameLanguage: UserStaffNameLanguage?,
        appTheme: Theme?,
    ): AppError? =
        UserSettingSyncer().postMutationAndRevertWhenException { old ->
            var new = old
            if (titleLanguage != null) {
                new = new.copy(userTitleLanguage = titleLanguage)
            }
            if (staffCharacterNameLanguage != null) {
                new = new.copy(userStaffNameLanguage = staffCharacterNameLanguage)
            }
            if (appTheme != null) {
                new = new.copy(appTheme = appTheme)
            }
            new
        }

    private suspend fun BrowserAuthOperationHandler.awaitAuthResult() =
        suspendCancellableCoroutine { cont ->
            cont.invokeOnCancellation {
                cancel()
            }

            getAuthResult { authToken ->
                if (authToken == null) {
                    Napier.d(tag = TAG) { "Authentication cancelled" }
                    cont.resumeWithException(CancellationException("Authentication cancelled."))
                } else {
                    Napier.d(tag = TAG) { "Authentication successful: $authToken" }
                    cont.resume(authToken)
                }
            }
        }
}
