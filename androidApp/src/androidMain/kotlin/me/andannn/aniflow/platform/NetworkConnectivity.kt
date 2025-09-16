package me.andannn.aniflow.platform

import android.content.Context
import android.net.NetworkCapabilities
import me.andannn.aniflow.data.NetworkConnectivity
import kotlin.getValue

class NetworkConnectivityImpl(
    private val context: Context,
) : NetworkConnectivity {
    private val connectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
    }

    override fun isConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
