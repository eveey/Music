package com.evastos.music.ui.spotify.artists.details

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.evastos.music.RxImmediateSchedulerRule
import com.evastos.music.TestUtil
import com.jakewharton.rxrelay2.PublishRelay
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ArtistDetailsViewModelTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val artistImageObserver = mock<Observer<String>>()
    private val artistNameObserver = mock<Observer<String>>()
    private val artistGenresObserver = mock<Observer<String>>()
    private val artistFollowersObserver = mock<Observer<Int>>()
    private val artistExternalUrlObserver = mock<Observer<String>>()
    private val networkConnectivityBannerObserver = mock<Observer<Boolean>>()

    private val networkConnectivityRelay = PublishRelay.create<Boolean>()

    private lateinit var viewModel: ArtistDetailsViewModel

    @Before
    fun setUp() {
        viewModel = ArtistDetailsViewModel()
        viewModel.imageLiveData.observeForever(artistImageObserver)
        viewModel.nameLiveData.observeForever(artistNameObserver)
        viewModel.genresLiveData.observeForever(artistGenresObserver)
        viewModel.followersLiveData.observeForever(artistFollowersObserver)
        viewModel.externalUrlLiveData.observeForever(artistExternalUrlObserver)
        viewModel.networkConnectivityBannerLiveData
                .observeForever(networkConnectivityBannerObserver)
    }

    @Test
    fun onCreate_withArtist_postsArtistData() {
        viewModel.onCreate(TestUtil.artist, networkConnectivityRelay)

        verify(artistImageObserver).onChanged("image_url")
        verify(artistNameObserver).onChanged("Iceage")
        verify(artistGenresObserver).onChanged("indie, dreampop, shoegaze")
        verify(artistFollowersObserver).onChanged(82773)
        verify(artistExternalUrlObserver).onChanged("external_url_spotify")
    }

    @Test
    fun onCreate_withNoNetwork_postsNetworkConnectivityBannerVisible() {
        viewModel.onCreate(TestUtil.artist, networkConnectivityRelay)

        networkConnectivityRelay.accept(false)

        verify(networkConnectivityBannerObserver).onChanged(true)
    }

    @Test
    fun onCreate_withNetwork_postsNetworkConnectivityBannerNotVisible() {
        viewModel.onCreate(TestUtil.artist, networkConnectivityRelay)

        networkConnectivityRelay.accept(true)

        verify(networkConnectivityBannerObserver).onChanged(false)
    }
}
