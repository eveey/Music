package com.evastos.music.ui.base

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.evastos.music.data.network.connectivity.NetworkConnectivityReceiver
import com.evastos.music.ui.base.network.connectivity.NetworkConnectivityObserver
import dagger.android.AndroidInjection
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var baseViewModel: BaseViewModel

    private var networkConnectivityObserver: NetworkConnectivityObserver? = null

    private val networkConnectivityReceiver = NetworkConnectivityReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        baseViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(BaseViewModel::class.java)
        networkConnectivityReceiver.observable?.let {
            baseViewModel.onCreate(it)
        }

        if (this is NetworkConnectivityObserver) networkConnectivityObserver = this
    }

    override fun onStart() {
        super.onStart()
        baseViewModel.networkConnectivityLiveData.observe(this,
            Observer { isConnected ->
                if (isConnected == true) {
                    networkConnectivityObserver?.onNetworkConnectivityAcquired()
                } else {
                    networkConnectivityObserver?.onNetworkConnectivityLost()
                }
            }
        )
    }

    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkConnectivityReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkConnectivityReceiver)
    }
}
