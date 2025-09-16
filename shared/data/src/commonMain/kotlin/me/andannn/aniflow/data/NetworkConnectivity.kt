package me.andannn.aniflow.data

/**
 * Abstracts network connectivity checks across different platforms.
 */
interface NetworkConnectivity {
    /**
     * Returns true if the device is currently connected to a network, false otherwise.
     */
    fun isConnected(): Boolean
}
