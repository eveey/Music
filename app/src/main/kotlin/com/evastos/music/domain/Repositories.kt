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

            val authRequestLiveEvent: SingleLiveEvent<AuthenticationRequest>
            val authErrorLiveData: MutableLiveData<String>
            val userLiveEvent: SingleLiveEvent<User>

            fun authenticateOrGetUser()
            fun handleAuthResponse(
                authResponse: AuthenticationResponse,
                disposables: CompositeDisposable
            )
        }
    }
}
