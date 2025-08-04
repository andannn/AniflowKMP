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
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.BrowserAuthOperationHandler
import me.andannn.aniflow.data.exceptions.toDataException
import me.andannn.aniflow.data.model.UserModel
import me.andannn.aniflow.database.MediaLibraryDao
import me.andannn.aniflow.database.schema.UserEntity
import me.andannn.aniflow.datastore.UserSettingPreferences
import me.andannn.aniflow.service.AniListService
import me.andannn.aniflow.service.ServerException
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
    override suspend fun startLoginProcessAndWaitResult() {
        authHandler.openBrowser()

        val authResult =
            try {
                authHandler.awaitAuthResult()
            } catch (exception: CancellationException) {
                // if the user cancelled the login process, we should not throw an exception
                // just log the cancellation and return
                Napier.w(tag = TAG) { "Login process was cancelled by user." }
                return
            }

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
        } catch (exception: ServerException) {
            throw exception.toDataException()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAuthedUser(): Flow<UserModel?> =
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
