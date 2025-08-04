package me.andannn.aniflow.platform

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import me.andannn.aniflow.data.AuthToken
import me.andannn.aniflow.data.BrowserAuthOperationHandler
import kotlin.coroutines.resume

private const val TAG = "BrowserAuthOperationHandler"
private const val CLIENT_ID = "14409"
private const val AUTH_URL =
    "https://anilist.co/api/v2/oauth/authorize?client_id={client_id}&response_type=token"

internal class BrowserAuthOperationHandlerImpl : BrowserAuthOperationHandler {
    private var authOperationContinuation: CancellableContinuation<AuthToken>? = null

    private var context: Context? = null

    fun setUpContext(context: Context) {
        this.context = context
    }

    fun clearContext() {
        this.context = null
    }

    override fun openBrowser() {
        val uri = AUTH_URL.replace("{client_id}", CLIENT_ID)
        context?.startActivity(Intent(Intent.ACTION_VIEW, uri.toUri()))
    }

    override suspend fun awaitAuthResult(): AuthToken =
        suspendCancellableCoroutine { cont ->
            if (authOperationContinuation != null) {
                error("Auth operation already in progress")
            }

            cont.invokeOnCancellation {
                authOperationContinuation = null
            }
            authOperationContinuation = cont
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

            authOperationContinuation?.resume(token) ?: run {
                Napier.e(tag = TAG) { "Auth result received but operation is cancelled. $token" }
            }

            authOperationContinuation = null
        }
    }

    fun onActivityResume() {
        if (authOperationContinuation == null) {
            Napier.d(tag = TAG) { "No auth operation in progress, just ignore this event." }
            // No operation in progress, nothing to do.
        } else {
            Napier.d(tag = TAG) { "activity resumed with no auth token cancel this operation." }
            authOperationContinuation?.cancel()
        }
    }
}
