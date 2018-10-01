package com.evastos.music.data.network.connectivity

import android.content.Context
import android.net.ConnectivityManager
import com.evastos.music.inject.qualifier.AppContext
import javax.inject.Inject

open class NetworkConnectivityProvider
@Inject constructor(@AppContext private val context: Context) {

    open fun isConnected(): Boolean {
        return with(context) {
            val connectivityManager =
                    this.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager?
            connectivityManager?.activeNetworkInfo?.let { info ->
                return@with info.isConnected
            }
            false
        }
    }
}
