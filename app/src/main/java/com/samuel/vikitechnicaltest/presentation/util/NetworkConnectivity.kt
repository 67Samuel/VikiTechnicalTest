package com.samuel.vikitechnicaltest.presentation.util
//
//import android.content.Context
//import android.net.ConnectivityManager
//import android.net.LinkProperties
//import android.net.Network
//import android.net.NetworkCapabilities
//import android.util.Log
//
//fun isOnline(context: Context): Boolean {
//    val connectivityManager =
//        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//    if (connectivityManager != null) {
//        val capabilities =
//            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
//        if (capabilities != null) {
//            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
//                return true
//            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
//                return true
//            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
//                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
//                return true
//            }
//        }
//    }
//    return false
//}
//
//fun registerNetworkCallback(callabck: ConnectivityManager.NetworkCallback) {
//    connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
//        override fun onAvailable(network : Network) {
//            Log.e(TAG, "The default network is now: " + network)
//        }
//
//        override fun onLost(network : Network) {
//            Log.e(TAG, "The application no longer has a default network. The last default network was " + network)
//        }
//
//        override fun onCapabilitiesChanged(network : Network, networkCapabilities : NetworkCapabilities) {
//            Log.e(TAG, "The default network changed capabilities: " + networkCapabilities)
//        }
//
//        override fun onLinkPropertiesChanged(network : Network, linkProperties : LinkProperties) {
//            Log.e(TAG, "The default network changed link properties: " + linkProperties)
//        }
//    })
//}