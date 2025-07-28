package me.andannn.aniflow.service

interface TokenProvider {
    /**
     * Get the access token.
     *
     * @return The access token, or null if not available.
     */
    suspend fun getAccessToken(): String?
}
