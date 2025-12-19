/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.platform

interface SnackBarMessageHandler {
    fun showSnackBarMessage(
        message: SnackBarMessage,
        callBack: (SharedSnackbarResult) -> Unit = {},
    )
}

enum class SharedSnackbarResult {
    Dismissed,
    ActionPerformed,
}

enum class SnackbarShowDuration {
    Short,
    Long,
    Indefinite,
}

interface SnackBarMessage {
    val message: String
    val duration: SnackbarShowDuration
    val actionLabel: String?
    val withDismissAction: Boolean
}
