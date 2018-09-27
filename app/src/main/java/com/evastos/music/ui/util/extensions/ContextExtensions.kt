package com.evastos.music.ui.util.extensions

import android.content.Context
import android.net.ConnectivityManager

fun Context?.isConnectedToNetwork(): Boolean {
    val connectivityManager =
            this?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager?
    connectivityManager?.activeNetworkInfo?.let { info ->
        return info.isConnected
    }
    return false
}
