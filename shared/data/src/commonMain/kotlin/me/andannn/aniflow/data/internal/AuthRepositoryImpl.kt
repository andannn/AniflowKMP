/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import me.andannn.aniflow.data.AppError
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.BrowserAuthOperationHandler
import me.andannn.aniflow.data.internal.exceptions.toError
import me.andannn.aniflow.data.model.UserModel
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

class AuthRepositoryImpl(
    private val service: AniListService,
    private val userPref: UserSettingPreferences,
    private val database: MediaLibraryDao,
    private val authHandler: BrowserAuthOperationHandler,
) : AuthRepository {
    @OptIn(ExperimentalTime::class)
    override suspend fun startLoginProcessAndWaitResult(): AppError? {
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
            return null
        } catch (exception: ServerException) {
            return exception.toError()
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
