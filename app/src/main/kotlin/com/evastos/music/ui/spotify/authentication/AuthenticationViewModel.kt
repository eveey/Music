package com.evastos.music.ui.spotify.authentication

import android.arch.lifecycle.MutableLiveData
import com.evastos.music.domain.Repositories
import com.evastos.music.ui.base.BaseViewModel
import com.spotify.sdk.android.authentication.AuthenticationResponse
import io.reactivex.Observable
import javax.inject.Inject

class AuthenticationViewModel
@Inject constructor(
    private val repository: Repositories.Spotify.Authentication
) : BaseViewModel() {

    val authRequestLiveEvent = repository.authRequestLiveEvent
    val authErrorLiveData = repository.authErrorLiveData
    val userLiveEvent = repository.userLiveEvent
    val networkConnectivityBannerLiveData = MutableLiveData<Boolean>()

    fun onCreate(networkConnectivityObservable: Observable<Boolean>?) {
        super.onCreate(networkConnectivityObservable) { isConnected ->
            networkConnectivityBannerLiveData.postValue(isConnected.not())
            if (isConnected) {
                authenticateOrGetUser()
            }
        }
        authenticateOrGetUser()
    }

    fun onRetry() {
        authenticateOrGetUser()
    }

    fun onAuthResponse(authResponse: AuthenticationResponse) {
        repository.handleAuthResponse(authResponse, disposables)
    }

    private fun authenticateOrGetUser() {
        repository.authenticateOrGetUser()
    }
}
