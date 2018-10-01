package com.evastos.music.domain.spotify.authentication

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.evastos.music.RxImmediateSchedulerRule
import com.evastos.music.TestUtil
import com.evastos.music.data.exception.ExceptionMappers
import com.evastos.music.data.exception.spotify.SpotifyException
import com.evastos.music.data.model.spotify.user.User
import com.evastos.music.data.persistence.prefs.PreferenceStore
import com.evastos.music.data.service.spotify.SpotifyService
import com.evastos.music.data.service.spotify.scopes.Scopes
import com.evastos.music.domain.Repositories
import com.evastos.music.domain.exception.ExceptionMessageProviders
import com.evastos.music.ui.util.DateTimeUtil
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.isNull
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthenticationRepositoryTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val spotifyService = mock<SpotifyService>()
    private val exceptionMapper = mock<ExceptionMappers.Spotify>()
    private val exceptionMessageProvider = mock<ExceptionMessageProviders.Spotify>()
    private val preferenceStore = mock<PreferenceStore>()
    private val spotifyRedirectUri = "spotifyRedirectUri"
    private val spotifyScopes = mock<Scopes.Spotify>()

    private val authRequestObserver = mock<Observer<AuthenticationRequest>>()
    private val authErrorObserver = mock<Observer<String>>()
    private val userObserver = mock<Observer<User>>()

    private var nowInMillis = 10L

    private lateinit var authenticationRepository: Repositories.Spotify.Authentication

    @Before
    fun setUp() {
        authenticationRepository = AuthenticationRepository(
            spotifyService = spotifyService,
            spotifyRedirectUri = spotifyRedirectUri,
            spotifyExceptionMapper = exceptionMapper,
            spotifyExceptionMessageProvider = exceptionMessageProvider,
            preferenceStore = preferenceStore,
            dateTimeUtil = TestDateTimeUtil(),
            spotifyScopes = spotifyScopes
        )

        authenticationRepository.authRequestLiveEvent.observeForever(authRequestObserver)
        authenticationRepository.authErrorLiveData.observeForever(authErrorObserver)
        authenticationRepository.userLiveEvent.observeForever(userObserver)
    }

    @Test
    fun authenticateOrGetUser_withTokenExpired_postsAuthRequestToken() {
        whenever(preferenceStore.authData).thenReturn(TestUtil.autDataExpired)

        authenticationRepository.authenticateOrGetUser()

        verify(authRequestObserver).onChanged(check {
            assertEquals(AuthenticationResponse.Type.TOKEN.name.toLowerCase(), it.responseType)
        })
        verifyNoMoreInteractions(userObserver)
    }

    @Test
    fun authenticateOrGetUser_withTokenExpired_clearsAuthError() {
        whenever(preferenceStore.authData).thenReturn(TestUtil.autDataExpired)

        authenticationRepository.authenticateOrGetUser()

        verify(authErrorObserver).onChanged(isNull())
        verifyNoMoreInteractions(userObserver)
    }

    @Test
    fun authenticateOrGetUser_withTokenValid_withUser_postsUser() {
        whenever(preferenceStore.user).thenReturn(TestUtil.user)
        whenever(preferenceStore.authData).thenReturn(TestUtil.autDataValid)

        authenticationRepository.authenticateOrGetUser()

        verify(userObserver).onChanged(TestUtil.user)
        verifyNoMoreInteractions(authRequestObserver)
    }

    @Test
    fun authenticateOrGetUser_withTokenValid_clearsAuthError() {
        whenever(preferenceStore.authData).thenReturn(TestUtil.autDataValid)

        authenticationRepository.authenticateOrGetUser()

        verify(authErrorObserver).onChanged(isNull())
        verifyNoMoreInteractions(authRequestObserver)
    }

    @Test
    fun handleAuthResponse_withToken_withUserSuccess_postsUser() {
        whenever(spotifyService.getUser()).thenReturn(Single.just(TestUtil.user))
        whenever(exceptionMapper.map(any())).thenReturn(SpotifyException.ClientException())

        authenticationRepository.handleAuthResponse(
            getAuthResponse(AuthenticationResponse.Type.TOKEN, "token"), CompositeDisposable()
        )

        verify(userObserver).onChanged(TestUtil.user)
        verifyNoMoreInteractions(authErrorObserver)
    }

    @Test
    fun handleAuthResponse_withToken_withUserError_postsError() {
        whenever(spotifyService.getUser()).thenReturn(Single.error(Throwable()))
        whenever(exceptionMapper.map(any())).thenReturn(SpotifyException.ClientException())
        whenever(exceptionMessageProvider.getMessage(any())).thenReturn("error_message")
        whenever(exceptionMessageProvider.getMessage(any<Throwable>()))
                .thenReturn("error_message")

        authenticationRepository.handleAuthResponse(
            getAuthResponse(AuthenticationResponse.Type.TOKEN, "token"), CompositeDisposable()
        )

        verify(authErrorObserver).onChanged("error_message")
        verifyNoMoreInteractions(userObserver)
    }

    @Test
    fun handleAuthResponse_withCode_doesNothing() {
        authenticationRepository.handleAuthResponse(
            getAuthResponse(AuthenticationResponse.Type.CODE), CompositeDisposable()
        )

        verifyNoMoreInteractions(authErrorObserver)
        verifyNoMoreInteractions(userObserver)
    }

    @Test
    fun handleAuthResponse_withError_postsAuthError() {
        whenever(exceptionMessageProvider.authErrorMessage).thenReturn("authErrorMessage")

        authenticationRepository.handleAuthResponse(
            getAuthResponse(AuthenticationResponse.Type.ERROR), CompositeDisposable()
        )

        verify(authErrorObserver).onChanged("authErrorMessage")
    }

    @Test
    fun handleAuthResponse_withEmpty_authenticates() {
        authenticationRepository.handleAuthResponse(
            getAuthResponse(AuthenticationResponse.Type.EMPTY), CompositeDisposable()
        )

        verify(authRequestObserver).onChanged(check {
            assertEquals(AuthenticationResponse.Type.TOKEN.name.toLowerCase(), it.responseType)
        })
        verifyNoMoreInteractions(authErrorObserver)
        verifyNoMoreInteractions(userObserver)
    }

    @Test
    fun handleAuthResponse_withUnknown_authenticates() {
        authenticationRepository.handleAuthResponse(
            getAuthResponse(AuthenticationResponse.Type.UNKNOWN), CompositeDisposable()
        )

        verify(authRequestObserver).onChanged(check {
            assertEquals(AuthenticationResponse.Type.TOKEN.name.toLowerCase(), it.responseType)
        })
        verifyNoMoreInteractions(authErrorObserver)
        verifyNoMoreInteractions(userObserver)
    }

    @Test
    fun handleAuthResponse_withTypeNull_authenticates() {
        authenticationRepository.handleAuthResponse(
            getAuthResponse(null), CompositeDisposable()
        )

        verify(authRequestObserver).onChanged(check {
            assertEquals(AuthenticationResponse.Type.TOKEN.name.toLowerCase(), it.responseType)
        })
        verifyNoMoreInteractions(authErrorObserver)
        verifyNoMoreInteractions(userObserver)
    }

    private inner class TestDateTimeUtil : DateTimeUtil() {
        override fun getNow(): Long {
            return nowInMillis
        }
    }

    private fun getAuthResponse(
        type: AuthenticationResponse.Type?,
        token: String? = null
    ): AuthenticationResponse =
            AuthenticationResponse.Builder()
                    .setType(type)
                    .setAccessToken(token)
                    .build()
}
