package com.samuel.vikitechnicaltest.presentation.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import com.samuel.vikitechnicaltest.presentation.selectcountry.SelectCountryEvents
import com.samuel.vikitechnicaltest.presentation.selectcountry.SelectCountryEvents.NetworkConnectionChanged
import javax.inject.Inject

class NetworkUtils @Inject constructor(
    val context: Context
) {
    private val TAG: String = "NetworkUtilsDebug"
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun isOnline(): Boolean {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }

    fun registerNetworkCallback(
        networkCallback: ConnectivityManager.NetworkCallback,
        onTriggerSelectCountryEvent: (event: SelectCountryEvents) -> Unit
    ) {
//        networkCallback = new ConnectivityManager.NetworkCallback()
//        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
//            override fun onAvailable(network : Network) {
//                Log.e(TAG, "The default network is now: " + network)
//                onTriggerSelectCountryEvent(NetworkConnectionChanged(true))
//            }
//
//            override fun onLost(network : Network) {
//                Log.e(TAG, "The application no longer has a default network. The last default network was " + network)
//                onTriggerSelectCountryEvent(NetworkConnectionChanged(false))
//            }
//        })
    }

}