package com.evastos.music.ui.spotify.authentication

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.evastos.music.R
import com.evastos.music.ui.base.BaseActivity
import com.evastos.music.ui.util.extensions.disable
import com.evastos.music.ui.util.extensions.enable
import com.spotify.sdk.android.authentication.AuthenticationClient
import kotlinx.android.synthetic.main.activity_authentication.authenticationView
import kotlinx.android.synthetic.main.activity_authentication.userActionButton

class AuthenticationActivity : BaseActivity() {

    companion object {
        private const val AUTH_REQUEST_CODE = 0x11
    }

    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
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
        })

        viewModel.userLiveEvent.observe(this, Observer { response ->
            response?.let { user ->
                val action = String.format(
                    getString(R.string.action_button_user),
                    user.displayName,
                    user.email
                )
                userActionButton.text = action
            }
        })

        viewModel.userButtonEnabledLiveData.observe(this, Observer { enabled ->
            if (enabled == true) {
                userActionButton.enable()
            } else {
                userActionButton.disable()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.onAuthResponse(AuthenticationClient.getResponse(resultCode, data))
    }
}
