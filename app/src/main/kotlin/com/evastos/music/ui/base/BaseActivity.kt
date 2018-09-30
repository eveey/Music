package com.evastos.music.ui.base

import android.arch.lifecycle.ViewModelProvider
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.evastos.music.data.network.connectivity.NetworkConnectivityReceiver
import com.evastos.music.ui.util.extensions.hideIfShown
import com.evastos.music.ui.util.extensions.showSnackbarForView
import dagger.android.AndroidInjection
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {

    companion object {
        private const val SNACKBAR_DELAY_MILLIS = 400L
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected val networkConnectivityReceiver = NetworkConnectivityReceiver()

    @get:LayoutRes
    abstract val layoutRes: Int

    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)
        AndroidInjection.inject(this)
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

    protected fun showSnackbar(
        view: View,
        snackbarMessage: String,
        actionMessage: String? = null,
        action: (() -> Unit)? = null
    ) {
        view.postDelayed({
            snackbar = showSnackbarForView(view, snackbarMessage, actionMessage, action)
        }, SNACKBAR_DELAY_MILLIS)
    }

    protected fun hideSnackbar() {
        snackbar.hideIfShown()
    }
}
