package com.evastos.music.domain.spotify.authentication

import android.arch.lifecycle.MutableLiveData
import com.evastos.music.BuildConfig
import com.evastos.music.data.model.spotify.User
import com.evastos.music.data.rx.applySchedulers
import com.evastos.music.data.service.spotify.SpotifyService
import com.evastos.music.data.service.spotify.scopes.Scopes
import com.evastos.music.data.storage.prefs.PreferenceStore
import com.evastos.music.domain.Repositories
import com.evastos.music.domain.livedata.SingleLiveEvent
import com.evastos.music.inject.qualifier.SpotifyRedirectUri
import com.evastos.music.ui.util.DateTimeUtil
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.spotify.sdk.android.authentication.AuthenticationResponse.Type
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class SpotifyAuthenticationRepository
@Inject constructor(
    private val spotifyService: SpotifyService,
    private val spotifyScopes: Scopes.Spotify,
    @SpotifyRedirectUri private val spotifyRedirectUri: String,
    private val preferenceStore: PreferenceStore,
    private val dateTimeUtil: DateTimeUtil
) : Repositories.Spotify.Authentication {

    companion object {
        private const val SEC_IN_MILLIS = 1000
    }

    override var authRequestLiveEvent = SingleLiveEvent<AuthenticationRequest>()
    override var authErrorLiveData = MutableLiveData<String>()
    override var userLiveEvent = SingleLiveEvent<User>()

    override fun authenticateOrGetUser() {
        if (isTokenExpired()) {
            authenticate()
        } else {
            userLiveEvent.postValue(preferenceStore.user)
        }
    }

    override fun authenticate() {
        authRequestLiveEvent.postValue(getAuthRequest(Type.TOKEN))
    }

    override fun handleAuthResponse(
        authResponse: AuthenticationResponse,
        disposables: CompositeDisposable
    ) {
        when (authResponse.type) {
            Type.TOKEN -> {
                preferenceStore.authToken = authResponse.accessToken
                preferenceStore.authTokenExpiresIn = authResponse.expiresIn
                preferenceStore.authTokenRefreshedAt = dateTimeUtil.getNow()
                authRequestLiveEvent.postValue(getAuthRequest(Type.CODE))
            }
            Type.CODE -> {
                preferenceStore.authCode = authResponse.code
                getUser(disposables)
            }
            Type.ERROR -> authErrorLiveData.postValue(authResponse.error)
            Type.EMPTY -> Timber.d("AuthResponse type is empty")
            Type.UNKNOWN -> Timber.d("AuthResponse type is unknown")
            null -> Timber.d("AuthResponse type is null")
        }
    }

    private fun getUser(disposables: CompositeDisposable) {
        disposables.add(
            spotifyService.getUser()
                    .applySchedulers()
                    .subscribe({
                        preferenceStore.user = it
                        userLiveEvent.postValue(it)
                    }, {
                        authErrorLiveData.postValue(it.message)
                    }))
    }

    private fun getAuthRequest(type: AuthenticationResponse.Type): AuthenticationRequest {
        return AuthenticationRequest.Builder(
            BuildConfig.SPOTIFY_CLIENT_ID,
            type,
            spotifyRedirectUri
        ).setShowDialog(false).setScopes(spotifyScopes.getScopes()).build()
    }

    private fun isTokenExpired(): Boolean {
        if (preferenceStore.authToken != null) {
            val timePassedInSeconds =
                    (dateTimeUtil.getNow() - preferenceStore.authTokenRefreshedAt) / SEC_IN_MILLIS
            if (timePassedInSeconds < preferenceStore.authTokenExpiresIn) {
                return false
            }
        }
        return true
    }
}
