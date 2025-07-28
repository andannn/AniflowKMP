package me.andannn.aniflow.service.token

import me.andannn.aniflow.service.TokenProvider

class DataStoreTokenProvider : TokenProvider {
    override suspend fun getAccessToken(): String? {
// TODO: Implement DataStore access token retrieval
        return "aa"
    }
}
