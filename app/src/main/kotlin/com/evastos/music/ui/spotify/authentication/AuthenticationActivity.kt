package com.evastos.music.ui.spotify.authentication

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.evastos.music.R
import com.evastos.music.ui.base.BaseActivity
import com.evastos.music.ui.spotify.artists.ArtistsActivity
import com.evastos.music.ui.util.extensions.setGone
import com.evastos.music.ui.util.extensions.setVisible
import com.spotify.sdk.android.authentication.AuthenticationClient
import kotlinx.android.synthetic.main.activity_authentication.authenticationView
import kotlinx.android.synthetic.main.activity_authentication.networkConnectivityBanner

class AuthenticationActivity : BaseActivity() {

    companion object {
        private const val AUTH_REQUEST_CODE = 0x11
    }

    override val layoutRes: Int = R.layout.activity_authentication

    private lateinit var viewModel: AuthenticationViewModel

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.let {
            title = getString(R.string.activity_title_authentication)
        }

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(AuthenticationViewModel::class.java)

        viewModel.authRequestLiveEvent.observe(this, Observer { request ->
            AuthenticationClient.openLoginActivity(this, AUTH_REQUEST_CODE, request)
        })

        viewModel.authErrorLiveData.observe(this, Observer { error ->
            error?.let {
                showSnackbar(authenticationView, it, getString(R.string.action_retry)) {
                    viewModel.onRetry()
                }
            }
            if (error == null) hideSnackbar()
        })

        viewModel.networkConnectivityBannerLiveData.observe(this, Observer { isVisible ->
            if (isVisible == true) {
                networkConnectivityBanner.setVisible()
            } else networkConnectivityBanner.setGone()
        })

        viewModel.userLiveEvent.observe(this, Observer { user ->
            startActivity(ArtistsActivity.newIntent(this))
            finishAffinity()
        })

        viewModel.onCreate(networkConnectivityReceiver.observable)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTH_REQUEST_CODE) {
            viewModel.onAuthResponse(AuthenticationClient.getResponse(resultCode, data))
        }
    }
}
