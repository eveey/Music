package com.evastos.music.domain.datasource.spotify.artists.search

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import android.arch.paging.PageKeyedDataSource
import com.evastos.music.RxImmediateSchedulerRule
import com.evastos.music.TestUtil
import com.evastos.music.data.exception.ExceptionMappers
import com.evastos.music.data.exception.spotify.SpotifyException
import com.evastos.music.data.model.spotify.item.artist.Artist
import com.evastos.music.data.network.connectivity.NetworkConnectivityProvider
import com.evastos.music.data.service.spotify.SpotifyService
import com.evastos.music.domain.exception.ExceptionMessageProviders
import com.evastos.music.domain.livedata.LoadingState
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ArtistsSearchDataSourceTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val spotifyService = mock<SpotifyService>()
    private val exceptionMapper = mock<ExceptionMappers.Spotify>()
    private val exceptionMessageProvider = mock<ExceptionMessageProviders.Spotify>()
    private val loadingStateObserver = mock<Observer<LoadingState>>()

    private val loadParams = PageKeyedDataSource.LoadParams(0, 100)
    private val loadCallback = mock<PageKeyedDataSource.LoadCallback<Int, Artist>>()
    private val loadInitialParams = mock<PageKeyedDataSource.LoadInitialParams<Int>>()
    private val loadInitialCallback = mock<PageKeyedDataSource.LoadInitialCallback<Int, Artist>>()

    private var networkIsConnected = true

    private lateinit var artistsSearchDataSource: ArtistsSearchDataSource

    @Before
    fun setUp() {
        whenever(exceptionMapper.map(any())).thenReturn(SpotifyException.UnknownException())
        whenever(exceptionMessageProvider.getMessage(any()))
                .thenReturn("error_message")
        whenever(exceptionMessageProvider.getMessage(any<Throwable>()))
                .thenReturn("error_message")

        artistsSearchDataSource = ArtistsSearchDataSource(
            query = "Iceage",
            spotifyService = spotifyService,
            exceptionMapper = exceptionMapper,
            exceptionMessageProvider = exceptionMessageProvider,
            networkConnectivityProvider = TestNetworkConnectivityProvider(),
            disposables = CompositeDisposable()
        )
        artistsSearchDataSource.loadingState.observeForever(loadingStateObserver)
    }

    @Test
    fun loadBefore_doesNothing() {
        artistsSearchDataSource.loadBefore(loadParams, loadCallback)

        verifyNoMoreInteractions(spotifyService)
        verifyNoMoreInteractions(loadingStateObserver)
    }

    @Test
    fun loadAfter_withSuccessResponse_callsOnResult() {
        whenever(spotifyService.search(any(), any(), any(), any(), any()))
                .thenReturn(Single.just(TestUtil.searchResponse))

        artistsSearchDataSource.loadAfter(loadParams, loadCallback)

        verify(loadingStateObserver, times(2)).onChanged(check {
            assertNotNull(it)
        })
        verify(loadCallback).onResult(TestUtil.artistList2, 1)
    }

    @Test
    fun loadAfter_withNoNetworkConnectivity_failsFast() {
        whenever(spotifyService.search(any(), any(), any(), any(), any()))
                .thenReturn(Single.just(TestUtil.searchResponse))
        networkIsConnected = false

        artistsSearchDataSource.loadAfter(loadParams, loadCallback)

        verify(loadingStateObserver).onChanged(check {
            assertNotNull(it)
        })
        verifyNoMoreInteractions(loadCallback)
    }

    @Test
    fun loadAfter_withErrorResponse_doesNotCallOnResult() {
        whenever(spotifyService.search(any(), any(), any(), any(), any()))
                .thenReturn(Single.error(Throwable()))

        artistsSearchDataSource.loadAfter(loadParams, loadCallback)

        verify(loadingStateObserver, times(2)).onChanged(check {
            assertNotNull(it)
        })
        verify(loadCallback, never()).onResult(any(), any())
    }

    @Test
    fun loadInitial_withSuccessResponse_callsOnResult() {
        whenever(spotifyService.search(any(), any(), any(), any(), any()))
                .thenReturn(Single.just(TestUtil.searchResponse))

        artistsSearchDataSource.loadInitial(loadInitialParams, loadInitialCallback)

        verify(loadingStateObserver, times(2)).onChanged(check {
            assertNotNull(it)
        })
        verify(loadInitialCallback).onResult(TestUtil.artistList2, -1, 1)
    }

    @Test
    fun loadInitial_withNoNetworkConnectivity_failsFast() {
        whenever(spotifyService.search(any(), any(), any(), any(), any()))
                .thenReturn(Single.just(TestUtil.searchResponse))
        networkIsConnected = false

        artistsSearchDataSource.loadInitial(loadInitialParams, loadInitialCallback)

        verify(loadingStateObserver).onChanged(check {
            assertNotNull(it)
        })
        verifyNoMoreInteractions(loadInitialCallback)
    }

    @Test
    fun loadInitial_withErrorResponse_doesNotCallOnResult() {
        whenever(spotifyService.search(any(), any(), any(), any(), any()))
                .thenReturn(Single.error(Throwable()))

        artistsSearchDataSource.loadInitial(loadInitialParams, loadInitialCallback)

        verify(loadingStateObserver, times(2)).onChanged(check {
            assertNotNull(it)
        })
        verify(loadInitialCallback, never()).onResult(any(), any(), any())
    }

    @Test
    fun retryAllFailed_withLoadAfter_withSuccessResponse_doesNotRetry() {
        whenever(spotifyService.search(any(), any(), any(), any(), any()))
                .thenReturn(Single.just(TestUtil.searchResponse))
        artistsSearchDataSource.loadAfter(loadParams, loadCallback)

        artistsSearchDataSource.retryAllFailed()

        verify(loadingStateObserver, times(2)).onChanged(check {
            assertNotNull(it)
        })
        verify(loadCallback).onResult(TestUtil.artistList2, 1)
        verify(spotifyService).search(
            eq("Iceage*"),
            check {
                assertEquals("artist", it.toString())
            },
            eq("from_token"),
            eq(20),
            eq(0)
        )
    }

    @Test
    fun retryAllFailed_withLoadAfter_withErrorResponse_doesRetry() {
        whenever(spotifyService.search(any(), any(), any(), any(), any()))
                .thenReturn(Single.error(Throwable()))
        artistsSearchDataSource.loadAfter(loadParams, loadCallback)
        whenever(spotifyService.search(any(), any(), any(), any(), any()))
                .thenReturn(Single.just(TestUtil.searchResponse))

        artistsSearchDataSource.retryAllFailed()

        verify(loadingStateObserver, times(4)).onChanged(check {
            assertNotNull(it)
        })
        verify(loadCallback).onResult(TestUtil.artistList2, 1)
        verify(spotifyService, times(2)).search(
            eq("Iceage*"),
            check {
                assertEquals("artist", it.toString())
            },
            eq("from_token"),
            eq(20),
            eq(0)
        )
    }

    @Test
    fun retryAllFailed_withLoadInitial_withSuccessResponse_doesNotRetry() {
        whenever(spotifyService.search(any(), any(), any(), any(), any()))
                .thenReturn(Single.just(TestUtil.searchResponse))
        artistsSearchDataSource.loadInitial(loadInitialParams, loadInitialCallback)

        artistsSearchDataSource.retryAllFailed()

        verify(loadingStateObserver, times(2)).onChanged(check {
            assertNotNull(it)
        })
        verify(loadInitialCallback).onResult(TestUtil.artistList2, -1, 1)
        verify(spotifyService).search(
            eq("Iceage*"),
            check {
                assertEquals("artist", it.toString())
            },
            eq("from_token"),
            eq(20),
            eq(0)
        )
    }

    @Test
    fun retryAllFailed_withLoadInitial_withErrorResponse_doesRetry() {
        whenever(spotifyService.search(any(), any(), any(), any(), any()))
                .thenReturn(Single.error(Throwable()))
        artistsSearchDataSource.loadInitial(loadInitialParams, loadInitialCallback)
        whenever(spotifyService.search(any(), any(), any(), any(), any()))
                .thenReturn(Single.just(TestUtil.searchResponse))

        artistsSearchDataSource.retryAllFailed()

        verify(loadingStateObserver, times(4)).onChanged(check {
            assertNotNull(it)
        })
        verify(loadInitialCallback).onResult(TestUtil.artistList2, -1, 1)
        verify(spotifyService, times(2)).search(
            eq("Iceage*"),
            check {
                assertEquals("artist", it.toString())
            },
            eq("from_token"),
            eq(20),
            eq(0)
        )
    }

    private inner class TestNetworkConnectivityProvider : NetworkConnectivityProvider(mock()) {
        override fun isConnected(): Boolean {
            return networkIsConnected
        }
    }
}
