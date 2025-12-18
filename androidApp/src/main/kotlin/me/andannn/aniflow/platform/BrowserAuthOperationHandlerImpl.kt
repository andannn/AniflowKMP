/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.platform

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import io.github.aakira.napier.Napier
import me.andannn.aniflow.BuildConfig
import me.andannn.aniflow.data.AuthToken
import me.andannn.aniflow.data.BrowserAuthOperationHandler

internal class BrowserAuthOperationHandlerImpl : BrowserAuthOperationHandler {
    private var authState: AuthState = AuthState.INITIAL

    private var context: Context? = null

    fun setUpContext(context: Context) {
        this.context = context
    }

    fun clearContext() {
        this.context = null
    }

    override fun getAuthResult(callBack: (AuthToken?) -> Unit) {
        if (authState is AuthState.Waiting) {
            error("Auth operation already in progress")
        }
        val uri = AUTH_URL.replace("{client_id}", CLIENT_ID)
        context?.startActivity(Intent(Intent.ACTION_VIEW, uri.toUri()))
        authState = AuthState.Waiting(callBack)
    }

    override fun cancel() {
        if (authState !is AuthState.Waiting) {
            Napier.d(tag = TAG) { "No auth operation in progress, nothing to cancel." }
            return
        }
        Napier.d(tag = TAG) { "operation cancelled" }
        authState = AuthState.INITIAL
    }

    fun onReceiveNewIntent(intent: Intent) {
        Napier.d(tag = TAG) { "onReceiveNewIntent $intent" }
        if (intent.data?.scheme == "animetracker") {
            // app received the redirect link from anilist.
            val rawUrl = intent.data?.toString()?.replace('#', '?')
            val token =
                rawUrl?.toUri()?.let { uri ->
                    AuthToken(
                        token = uri.getQueryParameter("access_token") ?: error("no token"),
                        expiresInTime =
                            uri.getQueryParameter("expires_in")?.toIntOrNull()
                                ?: error("no expires_in"),
                    )
                } ?: return

            Napier.d(tag = TAG) { "onReceiveNewIntent $token" }

            (authState as? AuthState.Waiting)?.callBack?.invoke(token)
            authState = AuthState.INITIAL
        }
    }

    fun onActivityResume() {
        Napier.d(tag = TAG) { "onActivityResume." }
        when (val authState = this.authState) {
            is AuthState.Waiting -> {
                Napier.d(tag = TAG) { "activity resumed with no auth token cancel this operation." }

                // If the auth operation is in progress, invoke the callback with null to indicate cancellation.
                authState.callBack(null)
                this.authState = AuthState.INITIAL
            }

            AuthState.INITIAL -> {
                Napier.d(tag = TAG) { "No auth operation in progress, just ignore this event." }
                // No operation in progress, nothing to do.
            }
        }
    }
}

private const val TAG = "BrowserAuthOperationHandler"
private const val CLIENT_ID = "14409"
private const val AUTH_URL =
    "https://anilist.co/api/v2/oauth/authorize?client_id={client_id}&response_type=token"

private sealed interface AuthState {
    data object INITIAL : AuthState

    data class Waiting(
        val callBack: (AuthToken?) -> Unit,
    ) : AuthState
}

object PresentationDummyHandler : BrowserAuthOperationHandler {
    override fun getAuthResult(callBack: (AuthToken?) -> Unit) {
        callBack(
            AuthToken(
                token = BuildConfig.PRESENTATION_TOKEN,
                expiresInTime = 3600000,
            ),
        )
    }

    override fun cancel() {
        // Do nothing
    }
}
