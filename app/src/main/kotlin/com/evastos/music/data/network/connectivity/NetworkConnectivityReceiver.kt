package com.evastos.music.data.network.connectivity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import dagger.android.DaggerBroadcastReceiver
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class NetworkConnectivityReceiver : DaggerBroadcastReceiver() {

    @Inject
    lateinit var networkConnectivityProvider: NetworkConnectivityProvider

    private val networkConnectivitySubject = PublishSubject.create<Boolean>()

    val observable = networkConnectivitySubject as? Observable<Boolean>

    /* Warning: this method for checking network connectivity will be deprecated.
      Use NetworkCapabilities available since API level 21 (Marshmallow)
     */
    @Suppress("DEPRECATION")
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            networkConnectivitySubject.onNext(networkConnectivityProvider.isConnected())
        }
    }
}
