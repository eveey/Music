package com.evastos.music.domain.spotify.artists

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.evastos.music.RxImmediateSchedulerRule
import com.evastos.music.TestUtil
import com.evastos.music.data.exception.ExceptionMappers
import com.evastos.music.data.exception.spotify.SpotifyException
import com.evastos.music.data.model.spotify.item.artist.Artist
import com.evastos.music.data.network.connectivity.NetworkConnectivityProvider
import com.evastos.music.data.persistence.prefs.PreferenceStore
import com.evastos.music.data.service.spotify.SpotifyService
import com.evastos.music.domain.Repositories
import com.evastos.music.domain.exception.ExceptionMessageProviders
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.isNull
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ArtistsRepositoryTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val spotifyService = mock<SpotifyService>()
    private val exceptionMapper = mock<ExceptionMappers.Spotify>()
    private val exceptionMessageProvider = mock<ExceptionMessageProviders.Spotify>()
    private val artistSuggestionsLiveDataObserver = mock<Observer<List<Artist>>>()
    private val preferenceStore = mock<PreferenceStore>()

    private val artistQueryObserver = mock<Observer<String>>()

    private lateinit var artistsRepository: Repositories.Spotify.Artists

    @Before
    fun setUp() {
        whenever(exceptionMapper.map(any())).thenReturn(SpotifyException.ClientException())
        whenever(spotifyService.search(any(), any(), any(), isNull(), isNull()))
                .thenReturn(Single.just(TestUtil.searchResponse))

        artistsRepository = ArtistsRepository(
            spotifyService = spotifyService,
            exceptionMapper = exceptionMapper,
            exceptionMessageProvider = exceptionMessageProvider,
            preferenceStore = preferenceStore,
            networkConnectivityProvider = TestNetworkConnectivityProvider()
        )
        artistsRepository.artistSearchLiveData.observeForever(artistQueryObserver)
    }

    @Test
    fun searchArtists_returnsArtistSearchListing() {
        val listing = artistsRepository.searchArtists("ZHU", CompositeDisposable())

        assertNotNull(listing.pagedList)
        assertNotNull(listing.loadingState)
        assertNotNull(listing.refresh)
        assertNotNull(listing.retry)
    }

    @Test
    fun searchArtists_withNullSearch_postsInitialArtistSearch() {
        whenever(preferenceStore.artistQuery).thenReturn(null)

        artistsRepository.searchArtists(null, CompositeDisposable())

        verify(artistQueryObserver).onChanged(eq("ZHU"))
    }

    @Test
    fun searchArtists_withNullSearch_withSavedQuery_postsSavedArtistQuery() {
        whenever(preferenceStore.artistQuery).thenReturn("saved_query")

        artistsRepository.searchArtists(null, CompositeDisposable())

        verify(artistQueryObserver).onChanged(eq("saved_query"))
    }

    @Test
    fun searchArtists_withNewSearch_postsNewArtistQuery() {
        whenever(preferenceStore.artistQuery).thenReturn("Lord Huron")

        artistsRepository.searchArtists("SNTS", CompositeDisposable())

        verify(artistQueryObserver).onChanged(eq("SNTS"))
    }

    @Test
    fun getArtistSuggestions_returnsArtistSuggestionsLiveData() {
        val artistSuggestionsLiveData =
                artistsRepository.getArtistSuggestions("Odesza", CompositeDisposable())

        assertNotNull(artistSuggestionsLiveData)
    }

    @Test
    fun getArtistSuggestions_callsSpotifyService() {
        artistsRepository.getArtistSuggestions("Clams Casino", CompositeDisposable())

        verify(spotifyService).search(
            eq("Clams Casino"),
            check {
                assertEquals("artist", it.toString())
            },
            eq("from_token"),
            isNull(),
            isNull()
        )
    }

    @Test
    fun getArtistSuggestions_withSuccess_postsArtistSuggestions() {
        artistsRepository.getArtistSuggestions("Heist", CompositeDisposable())
                .observeForever(artistSuggestionsLiveDataObserver)

        verify(artistSuggestionsLiveDataObserver).onChanged(TestUtil.artistList2)
    }

    private inner class TestNetworkConnectivityProvider : NetworkConnectivityProvider(mock()) {
        override fun isConnected(): Boolean {
            return true
        }
    }
}
