package com.evastos.music.ui.spotify.artists

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import com.evastos.music.RxImmediateSchedulerRule
import com.evastos.music.TestUtil
import com.evastos.music.data.model.spotify.item.artist.Artist
import com.evastos.music.domain.Repositories
import com.evastos.music.domain.livedata.LoadingState
import com.jakewharton.rxrelay2.PublishRelay
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.isNull
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ArtistsViewModelTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val repository = mock<Repositories.Spotify.Artists>()

    private val artistsLiveDataObserver = mock<Observer<PagedList<Artist>>>()
    private val loadingStateLiveDataObserver = mock<Observer<LoadingState>>()
    private val artistSuggestionsLiveDataObserver = mock<Observer<List<Artist>>>()
    private val artistDetailsLiveDataObserver = mock<Observer<Artist>>()
    private val artistSearchLiveDataObserver = mock<Observer<String>>()
    private val networkConnectivityBannerObserver = mock<Observer<Boolean>>()

    private val networkConnectivityRelay = PublishRelay.create<Boolean>()
    private val artistSearchLiveData = MutableLiveData<String>()

    private lateinit var viewModel: ArtistsViewModel

    @Before
    fun setUp() {
        whenever(repository.searchArtists(isNull(), any()))
                .thenReturn(TestUtil.artistsListing1)
        whenever(repository.searchArtists(eq("Interpol"), any()))
                .thenReturn(TestUtil.artistsListing2)
        whenever(repository.searchArtists(eq("No Rome"), any()))
                .thenReturn(TestUtil.artistsListing3)

        whenever(repository.getArtistSuggestions(eq("Not"), any()))
                .thenReturn(TestUtil.artistSuggestions1)
        whenever(repository.getArtistSuggestions(eq("Nothing"), any()))
                .thenReturn(TestUtil.artistSuggestions2)

        whenever(repository.artistSearchLiveData).thenReturn(artistSearchLiveData)

        viewModel = ArtistsViewModel(repository)
        viewModel.artistsLiveData.observeForever(artistsLiveDataObserver)
        viewModel.loadingStateLiveData.observeForever(loadingStateLiveDataObserver)
        viewModel.artistSuggestionsLiveData.observeForever(artistSuggestionsLiveDataObserver)
        viewModel.artistDetailsLiveData.observeForever(artistDetailsLiveDataObserver)
        viewModel.artistSearchLiveData.observeForever(artistSearchLiveDataObserver)
        viewModel.networkConnectivityBannerLiveData
                .observeForever(networkConnectivityBannerObserver)
    }

    @After
    fun tearDown() {
        reset(TestUtil.artistsRetry1)
        reset(TestUtil.artistsRetry2)
        reset(TestUtil.artistsRetry3)
        reset(TestUtil.artistsRefresh1)
        reset(TestUtil.artistsRefresh2)
        reset(TestUtil.artistsRefresh3)
    }

    @Test
    fun init_withInitialArtists_postsInitialArtists() {
        initialLoad()

        verify(artistsLiveDataObserver).onChanged(TestUtil.artistPagedList1)
        verify(loadingStateLiveDataObserver).onChanged(TestUtil.loading)
        verify(loadingStateLiveDataObserver).onChanged(TestUtil.success)
    }

    @Test
    fun init_withInitialArtists_doesNotPostSuggestionsAndArtistDetails() {
        initialLoad()

        verify(artistSuggestionsLiveDataObserver, never()).onChanged(any())
        verify(artistDetailsLiveDataObserver, never()).onChanged(any())
    }

    @Test
    fun onRetry_withInitialArtists_retriesSearchArtists() {
        initialLoad()

        viewModel.onRetry()

        verify(TestUtil.artistsRetry1).invoke()
        verify(TestUtil.artistsRetry2, never()).invoke()
    }

    @Test
    fun onRetry_withSearchedArtists_retriesSearchArtists() {
        initialLoad()
        viewModel.onSearchQuerySubmit("Interpol")

        viewModel.onRetry()

        verify(TestUtil.artistsRetry2).invoke()
        verify(TestUtil.artistsRetry1, never()).invoke()
    }

    @Test
    fun onRefresh_withInitialArtists_refreshesInitialArtists() {
        initialLoad()

        viewModel.onRefresh()

        verify(TestUtil.artistsRefresh1).invoke()
        verify(TestUtil.artistsRefresh2, never()).invoke()
    }

    @Test
    fun onRefresh_withSearchedArtists_refreshesSearchedArtists() {
        initialLoad()
        viewModel.onSearchQuerySubmit("Interpol")

        viewModel.onRefresh()

        verify(TestUtil.artistsRefresh2).invoke()
        verify(TestUtil.artistsRefresh1, never()).invoke()
    }

    @Test
    fun onSearchQuerySubmit_postsSearchedArtistsList() {
        initialLoad()

        viewModel.onSearchQuerySubmit("Interpol")

        verify(artistsLiveDataObserver).onChanged(TestUtil.artistPagedList2)
        verify(loadingStateLiveDataObserver).onChanged(TestUtil.loading)
        verify(loadingStateLiveDataObserver).onChanged(TestUtil.error)
    }

    @Test
    fun onSearchQuerySubmitTwice_postsNextSearchedArtistList() {
        initialLoad()

        viewModel.onSearchQuerySubmit("Interpol")
        viewModel.onSearchQuerySubmit("No Rome")

        verify(artistsLiveDataObserver).onChanged(TestUtil.artistPagedList2)
        verify(artistsLiveDataObserver).onChanged(TestUtil.artistPagedList3)
    }

    @Test
    fun onSearchQuerySubmit_doesNotPostSuggestionsAndArtistDetails() {
        initialLoad()

        viewModel.onSearchQuerySubmit("No Rome")

        verify(artistDetailsLiveDataObserver, never()).onChanged(any())
        verify(artistDetailsLiveDataObserver, never()).onChanged(any())
    }

    @Test
    fun onSearchQueryChange_postsArtistSuggestions() {
        initialLoad()

        viewModel.onSearchQueryChange("Not")

        verify(artistSuggestionsLiveDataObserver).onChanged(check { suggestions ->
            assertEquals(1, suggestions.size)
            assertEquals(TestUtil.artist, suggestions[0])
        })
    }

    @Test
    fun onSearchQueryChange_withSecondChange_postsNextArtistSuggestions() {
        initialLoad()

        viewModel.onSearchQueryChange("Not")
        viewModel.onSearchQueryChange("Nothing")

        verify(artistSuggestionsLiveDataObserver).onChanged(TestUtil.artistList1)
        verify(artistSuggestionsLiveDataObserver).onChanged(TestUtil.artistList2)
    }

    @Test
    fun onArtistClick_postsArtistDetails() {
        initialLoad()

        viewModel.onArtistClick(TestUtil.artist)

        verify(artistDetailsLiveDataObserver).onChanged(TestUtil.artist)
    }

    @Test
    fun onArtistSearchValue_postsArtistSearch() {
        initialLoad()

        viewModel.artistSearchLiveData.postValue("New search")

        verify(artistSearchLiveDataObserver).onChanged("New search")
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

    private fun initialLoad() {
        TestUtil.artists1.value = TestUtil.artistPagedList1
        TestUtil.artistsLoadingState1.value = TestUtil.loading
        TestUtil.artistsLoadingState1.value = TestUtil.success

        TestUtil.artists2.value = TestUtil.artistPagedList2
        TestUtil.artistsLoadingState2.value = TestUtil.loading
        TestUtil.artistsLoadingState2.value = TestUtil.error

        TestUtil.artists3.value = TestUtil.artistPagedList3
        TestUtil.artistsLoadingState3.value = TestUtil.loading
        TestUtil.artistsLoadingState3.value = TestUtil.success

        TestUtil.artistSuggestions1.value = TestUtil.artistList1
        TestUtil.artistSuggestions2.value = TestUtil.artistList2
    }
}
