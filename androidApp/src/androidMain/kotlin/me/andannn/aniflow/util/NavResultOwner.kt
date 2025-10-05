/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.util

import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositeKeyHashCode
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.currentCompositeKeyHashCode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.savedstate.SavedState
import androidx.savedstate.serialization.decodeFromSavedState
import androidx.savedstate.serialization.encodeToSavedState
import kotlinx.serialization.KSerializer

/**
 * Provides a way to store and retrieve results from screens.
 */
val LocalNavResultOwner =
    androidx.compose.runtime.staticCompositionLocalOf<NavResultOwner> {
        error("No RootNavigator provided")
    }

interface NavResultOwner {
    fun setNavResult(
        requestKey: String,
        result: SavedState,
    )
}

fun <T> NavResultOwner.setNavResult(
    requestKey: String,
    result: T,
    serializer: KSerializer<T>,
) {
    setNavResult(
        requestKey = requestKey,
        result = encodeToSavedState(serializer, result),
    )
}

@Composable
fun rememberNavResultOwner(): NavResultOwner {
    val navResultStore: NavResultStore = rememberNavResultStore()

    return remember(navResultStore) {
        NavResultOwnerImpl(navResultStore)
    }
}

@Composable
fun <T> LaunchNavResultHandler(
    requestKey: String,
    resultSerializer: KSerializer<T>,
    onResult: (T) -> Unit,
) {
    LaunchNavResultHandler(
        requestKey = requestKey,
        onResult = { savedState ->
            onResult(decodeFromSavedState(resultSerializer, savedState))
        },
    )
}

@Composable
fun LaunchNavResultHandler(
    requestKey: String,
    onResult: (SavedState) -> Unit,
) {
    val navResultOwner = LocalNavResultOwner.current as NavResultOwnerImpl

    val currentOnResult by rememberUpdatedState(onResult)
    val composeHashCode = currentCompositeKeyHashCode
    DisposableEffect(requestKey, onResult, currentOnResult) {
        if (navResultOwner.navResultStore.containsKey(requestKey)) {
            navResultOwner.navResultStore.remove(requestKey)?.let { result ->
                currentOnResult.invoke(result)
            }
        } else {
            navResultOwner.setNavResultListener(
                requestKey = requestKey,
                composeHashCode = composeHashCode,
                listener = {
                    currentOnResult.invoke(it)
                },
            )
        }

        onDispose {
            navResultOwner.clearNavResultListener(
                requestKey = requestKey,
                composeHashCode = composeHashCode,
            )
        }
    }
}

@Composable
internal fun rememberNavResultStore(): NavResultStore =
    rememberSaveable(
        saver =
            Saver(
                save = {
                    it.results
                },
                restore = {
                    NavResultStore(it)
                },
            ),
    ) {
        NavResultStore()
    }

internal class NavResultStore(
    internal val results: MutableMap<String, Bundle> = mutableMapOf(),
) : MutableMap<String, Bundle> by results

internal class NavResultOwnerImpl(
    val navResultStore: NavResultStore,
) : NavResultOwner {
    private val resultListeners = mutableMapOf<RequestListenerKey, (Bundle) -> Unit>()

    override fun setNavResult(
        requestKey: String,
        result: Bundle,
    ) {
        val listeners =
            resultListeners
                .filter {
                    it.key.requestKey == requestKey
                }.values

        if (listeners.isEmpty()) {
            navResultStore.results.put(requestKey, result)
            return
        }

        listeners.forEach {
            it.invoke(result)
        }
    }

    fun setNavResultListener(
        requestKey: String,
        composeHashCode: CompositeKeyHashCode,
        listener: (Bundle) -> Unit,
    ) {
        resultListeners.put(RequestListenerKey(requestKey, composeHashCode), listener)
    }

    fun clearNavResultListener(
        requestKey: String,
        composeHashCode: CompositeKeyHashCode,
    ) {
        resultListeners.remove(RequestListenerKey(requestKey, composeHashCode))
    }

    private data class RequestListenerKey(
        val requestKey: String,
        val composeHashCode: CompositeKeyHashCode,
    )
}
