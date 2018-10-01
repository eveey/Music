package com.evastos.music.data.network.connectivity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.jakewharton.rxrelay2.PublishRelay
import dagger.android.DaggerBroadcastReceiver
import io.reactivex.Observable
import javax.inject.Inject

class NetworkConnectivityReceiver : DaggerBroadcastReceiver() {

    @Inject
    lateinit var networkConnectivityProvider: NetworkConnectivityProvider

    private val networkConnectivityRelay = PublishRelay.create<Boolean>()

    val observable = networkConnectivityRelay as? Observable<Boolean>

    /* Warning: this method for checking network connectivity will be deprecated.
      Use NetworkCapabilities available since API level 21 (Marshmallow)
     */
    @Suppress("DEPRECATION")
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            networkConnectivityRelay.accept(networkConnectivityProvider.isConnected())
        }
    }
}
