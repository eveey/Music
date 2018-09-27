package com.evastos.music.data.network.connectivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.evastos.music.ui.util.extensions.isConnectedToNetwork
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class NetworkConnectivityReceiver : BroadcastReceiver() {

    private val networkConnectivitySubject = PublishSubject.create<Boolean>()

    val observable = networkConnectivitySubject as? Observable<Boolean>

    /* Warning: this method for checking network connectivity will be deprecated.
      Use NetworkCapabilities available since API level 21 (Marshmallow)
     */
    @Suppress("DEPRECATION")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            networkConnectivitySubject.onNext(context.isConnectedToNetwork())
        }
    }
}
