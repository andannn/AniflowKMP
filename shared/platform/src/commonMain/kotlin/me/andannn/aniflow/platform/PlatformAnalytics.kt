/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.platform

interface PlatformAnalytics {
    fun logEvent(
        event: String,
        params: Map<String, String> = emptyMap(),
    )

    fun recordException(throwable: Throwable)
}
