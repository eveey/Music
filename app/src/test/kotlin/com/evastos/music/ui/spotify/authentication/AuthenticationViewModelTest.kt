package com.evastos.music.ui.spotify.authentication

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.evastos.music.RxImmediateSchedulerRule
import com.evastos.music.TestUtil
import com.evastos.music.data.model.spotify.user.User
import com.evastos.music.domain.Repositories
import com.evastos.music.domain.livedata.SingleLiveEvent
import com.jakewharton.rxrelay2.PublishRelay
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthenticationViewModelTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val repository = mock<Repositories.Spotify.Authentication>()

    private val authRequestLiveEventObserver = mock<Observer<AuthenticationRequest>>()
    private val authErrorLiveDataObserver = mock<Observer<String>>()
    private val userLiveEventObserver = mock<Observer<User>>()
    private val networkConnectivityBannerObserver = mock<Observer<Boolean>>()

    private val networkConnectivityRelay = PublishRelay.create<Boolean>()
    private val authRequestLiveEvent = SingleLiveEvent<AuthenticationRequest>()
    private val authErrorLiveData = SingleLiveEvent<String>()
    private val userLiveEvent = SingleLiveEvent<User>()

    private val authenticationRequest = mock<AuthenticationRequest>()
    private val authenticationResponse = mock<AuthenticationResponse>()

    private lateinit var viewModel: AuthenticationViewModel

    @Before
    fun setUp() {
        whenever(repository.authRequestLiveEvent).thenReturn(authRequestLiveEvent)
        whenever(repository.authErrorLiveData).thenReturn(authErrorLiveData)
        whenever(repository.userLiveEvent).thenReturn(userLiveEvent)

        viewModel = AuthenticationViewModel(repository)
        viewModel.authRequestLiveEvent.observeForever(authRequestLiveEventObserver)
        viewModel.authErrorLiveData.observeForever(authErrorLiveDataObserver)
        viewModel.userLiveEvent.observeForever(userLiveEventObserver)
        viewModel.networkConnectivityBannerLiveData
                .observeForever(networkConnectivityBannerObserver)
    }

    @Test
    fun onAuthRequestValue_postsAuthenticationRequest() {
        authRequestLiveEvent.postValue(authenticationRequest)

        verify(authRequestLiveEventObserver).onChanged(authenticationRequest)
    }

    @Test
    fun onAuthErrorValue_postsAuthError() {
        authErrorLiveData.postValue("authErrorLiveData")

        verify(authErrorLiveDataObserver).onChanged("authErrorLiveData")
    }

    @Test
    fun onUserValue_postsUser() {
        userLiveEvent.postValue(TestUtil.user)

        verify(userLiveEventObserver).onChanged(TestUtil.user)
    }

    @Test
    fun onCreate_authenticatesOrGetsUser() {
        viewModel.onCreate(networkConnectivityRelay)

        verify(repository).authenticateOrGetUser()
    }

    @Test
    fun onCreate_withNetworkAcquired_authenticatesOrGetsUser() {
        viewModel.onCreate(networkConnectivityRelay)

        networkConnectivityRelay.accept(true)

        verify(repository, times(2)).authenticateOrGetUser()
    }

    @Test
    fun onRetry_authenticatesOrGetsUser() {
        viewModel.onRetry()

        verify(repository).authenticateOrGetUser()
    }

    @Test
    fun onAuthResponse_repositoryHandlesAuthResponse() {
        viewModel.onAuthResponse(authenticationResponse)

        verify(repository).handleAuthResponse(eq(authenticationResponse), any())
    }

    @Test
    fun onCreate_withNoNetwork_postsNetworkConnectivityBannerVisible() {
        viewModel.onCreate(networkConnectivityRelay)

        networkConnectivityRelay.accept(false)

        verify(networkConnectivityBannerObserver).onChanged(true)
    }

    @Test
    fun onCreate_withNetwork_postsNetworkConnectivityBannerNotVisible() {
        viewModel.onCreate(networkConnectivityRelay)

        networkConnectivityRelay.accept(true)

        verify(networkConnectivityBannerObserver).onChanged(false)
    }
}
