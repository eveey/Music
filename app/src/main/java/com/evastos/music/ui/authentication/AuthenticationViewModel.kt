package com.evastos.music.ui.authentication

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import com.evastos.music.data.model.spotify.User
import com.evastos.music.domain.Repositories
import com.evastos.music.ui.base.BaseViewModel
import com.spotify.sdk.android.authentication.AuthenticationResponse
import javax.inject.Inject

class AuthenticationViewModel
@Inject constructor(
    private val repository: Repositories.Spotify.Authentication
) : BaseViewModel() {

    val authRequestLiveEvent = repository.authRequestLiveEvent
    val authErrorLiveData = repository.authErrorLiveData
    val userLiveEvent = MediatorLiveData<User>()
    val userButtonEnabledLiveData = MutableLiveData<Boolean>()
            .apply {
                postValue(false)
            }

    init {
        userLiveEvent.addSource(repository.userLiveEvent) {
            userLiveEvent.postValue(it)
            userButtonEnabledLiveData.postValue(true)
        }
    }

    fun onStart() {
        repository.authenticateOrGetUser()
    }

    fun onRetry() {
        repository.authenticate()
    }

    fun onAuthResponse(authResponse: AuthenticationResponse) {
        repository.handleAuthResponse(authResponse, disposables)
    }
}
