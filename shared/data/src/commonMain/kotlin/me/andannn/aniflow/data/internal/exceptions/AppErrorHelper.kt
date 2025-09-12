/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal.exceptions

import me.andannn.aniflow.data.AppError
import me.andannn.aniflow.service.AniListServerException
import me.andannn.aniflow.service.ServerException

internal fun Throwable.toError(): AppError =
    when (this) {
        is AniListServerException -> AppError.ServerError(statusCode = statusCode, message = message)
        else -> AppError.OtherError(message ?: "Unknown error")
    }
