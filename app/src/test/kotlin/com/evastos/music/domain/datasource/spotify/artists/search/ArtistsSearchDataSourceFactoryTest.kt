package com.evastos.music.domain.datasource.spotify.artists.search

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.evastos.music.data.network.connectivity.NetworkConnectivityProvider
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ArtistsSearchDataSourceFactoryTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val sourceLiveDataObserver = mock<Observer<ArtistsSearchDataSource>>()

    private lateinit var artistsSearchDataSourceFactory: ArtistsSearchDataSourceFactory

    @Before
    fun setUp() {
        artistsSearchDataSourceFactory = ArtistsSearchDataSourceFactory(
            query = "Iceage",
            spotifyService = mock(),
            exceptionMapper = mock(),
            exceptionMessageProvider = mock(),
            disposables = CompositeDisposable(),
            networkConnectivityProvider = NetworkConnectivityProvider(mock())
        )
    }

    @Test
    fun create_postsArtistsSearchSourceLiveData() {
        artistsSearchDataSourceFactory.artistsSearchSourceLiveData
                .observeForever(sourceLiveDataObserver)

        val artistsSearchSourceLiveData = artistsSearchDataSourceFactory.create()

        verify(sourceLiveDataObserver)
                .onChanged(artistsSearchSourceLiveData as ArtistsSearchDataSource)
    }
}
