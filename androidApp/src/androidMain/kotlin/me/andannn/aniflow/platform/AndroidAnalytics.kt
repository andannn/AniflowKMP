/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.platform

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import io.github.aakira.napier.Napier
import me.andannn.aniflow.data.PlatformAnalytics

private const val TAG = "PlatformAnalyticsImpl"

class AndroidAnalytics : PlatformAnalytics {
    override fun logEvent(
        event: String,
        params: Map<String, String>,
    ) {
        Napier.d(tag = TAG) { "logEvent: event :$event, params $params" }
        Firebase.analytics.logEvent(
            event,
            Bundle().apply {
                params.forEach { (key, value) ->
                    this.putString(key, value)
                }
            },
        )
    }

    override fun recordException(throwable: Throwable) {
        Firebase.crashlytics.recordException(throwable)
    }
}
