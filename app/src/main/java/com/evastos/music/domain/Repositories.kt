package com.evastos.music.domain

import android.arch.lifecycle.MutableLiveData
import com.evastos.music.data.model.spotify.User
import com.evastos.music.domain.livedata.SingleLiveEvent
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import io.reactivex.disposables.CompositeDisposable

interface Repositories {

    interface Spotify {

        interface Authentication {

            var authRequestLiveEvent: SingleLiveEvent<AuthenticationRequest>
            var authErrorLiveData: MutableLiveData<String>
            var userLiveEvent: SingleLiveEvent<User>

            fun authenticateOrGetUser()
            fun authenticate()
            fun handleAuthResponse(
                authResponse: AuthenticationResponse,
                disposables: CompositeDisposable
            )
        }
    }
}