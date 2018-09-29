package com.evastos.music.domain.spotify.authentication

import android.arch.lifecycle.MutableLiveData
import com.evastos.music.BuildConfig
import com.evastos.music.data.exception.ExceptionMappers
import com.evastos.music.data.model.spotify.User
import com.evastos.music.data.rx.applySchedulers
import com.evastos.music.data.rx.mapException
import com.evastos.music.data.service.spotify.SpotifyService
import com.evastos.music.data.service.spotify.scopes.Scopes
import com.evastos.music.data.storage.prefs.PreferenceStore
import com.evastos.music.domain.Repositories
import com.evastos.music.domain.exception.ExceptionMessageProviders
import com.evastos.music.domain.livedata.SingleLiveEvent
import com.evastos.music.domain.model.AuthData
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
    private val dateTimeUtil: DateTimeUtil,
    private val spotifyExceptionMapper: ExceptionMappers.Spotify,
    private val spotifyExceptionMessageProvider: ExceptionMessageProviders.Spotify
) : Repositories.Spotify.Authentication {

    companion object {
        private const val SEC_IN_MILLIS = 1000L
    }

    override val authRequestLiveEvent = SingleLiveEvent<AuthenticationRequest>()
    override val authErrorLiveData = MutableLiveData<String>()
    override val userLiveEvent = SingleLiveEvent<User>()

    override fun authenticateOrGetUser() {
        authErrorLiveData.postValue(null)
        if (isTokenExpired()) {
            authenticate()
        } else {
            userLiveEvent.postValue(preferenceStore.user)
        }
    }

    private fun authenticate() {
        clearAuthData()
        authRequestLiveEvent.postValue(getAuthRequest(Type.TOKEN))
    }

    override fun handleAuthResponse(
        authResponse: AuthenticationResponse,
        disposables: CompositeDisposable
    ) {
        when (authResponse.type) {
            Type.TOKEN -> {
                preferenceStore.authData = AuthData(
                    authToken = authResponse.accessToken,
                    authTokenExpiresIn = authResponse.expiresIn * SEC_IN_MILLIS,
                    authTokenRefreshedAt = dateTimeUtil.getNow()
                )
                getUser(disposables)
            }
            Type.CODE -> Timber.d("AuthResponse type is code")
            Type.ERROR -> {
                authErrorLiveData.postValue(spotifyExceptionMessageProvider.authErrorMessage)
            }
            Type.EMPTY -> {
                Timber.d("AuthResponse type is empty")
                authenticate()
            }
            Type.UNKNOWN -> {
                Timber.d("AuthResponse type is unknown")
                authenticate()
            }
            null -> {
                Timber.d("AuthResponse type is null")
                authenticate()
            }
        }
    }

    private fun getUser(disposables: CompositeDisposable) {
        disposables.clear()
        disposables.add(
            spotifyService.getUser()
                    .mapException(spotifyExceptionMapper)
                    .applySchedulers()
                    .subscribe({
                        preferenceStore.user = it
                        userLiveEvent.postValue(it)
                    }, {
                        val errorMessage = spotifyExceptionMessageProvider.getMessage(it)
                        authErrorLiveData.postValue(errorMessage)
                    }))
    }

    private fun isTokenExpired(): Boolean {
        preferenceStore.authData?.let {
            if ((dateTimeUtil.getNow() - it.authTokenRefreshedAt) < it.authTokenExpiresIn)
                return false
        }
        return true
    }

    private fun clearAuthData() {
        preferenceStore.authData = null
        preferenceStore.user = null
    }

    private fun getAuthRequest(type: AuthenticationResponse.Type): AuthenticationRequest =
            AuthenticationRequest.Builder(
                BuildConfig.SPOTIFY_CLIENT_ID,
                type,
                spotifyRedirectUri
            )
                    .setShowDialog(false)
                    .setScopes(spotifyScopes.getScopes())
                    .build()
}
