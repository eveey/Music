package com.evastos.music.ui.base.network.connectivity

/**
 * Observes the network connectivity change events.
 */
interface NetworkConnectivityObserver {

    fun onNetworkConnectivityAcquired()

    fun onNetworkConnectivityLost()
}
