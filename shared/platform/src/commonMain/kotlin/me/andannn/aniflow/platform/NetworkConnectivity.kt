/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.platform

/**
 * Abstracts network connectivity checks across different platforms.
 */
interface NetworkConnectivity {
    /**
     * Returns true if the device is currently connected to a network, false otherwise.
     */
    fun isConnected(): Boolean
}
