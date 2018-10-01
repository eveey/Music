package com.evastos.music.ui.spotify.artists

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedList
import com.evastos.music.data.model.spotify.item.artist.Artist
import com.evastos.music.domain.Repositories
import com.evastos.music.domain.livedata.Listing
import com.evastos.music.domain.livedata.LoadingState
import com.evastos.music.domain.livedata.SingleLiveEvent
import com.evastos.music.ui.base.BaseViewModel
import io.reactivex.Observable
import javax.inject.Inject

class ArtistsViewModel
@Inject constructor(
    private val repository: Repositories.Spotify.Artists
) : BaseViewModel() {

    // ARTISTS
    val artistsLiveData = MediatorLiveData<PagedList<Artist>>()

    // LOADING STATE
    val loadingStateLiveData = MediatorLiveData<LoadingState>()

    // ARTIST SUGGESTIONS
    val artistSuggestionsLiveData = MediatorLiveData<List<Artist>>()

    // ARTIST DETAILS
    val artistDetailsLiveData = SingleLiveEvent<Artist>()

    // ARTIST SEARCH NAME
    val artistSearchLiveData = repository.artistSearchLiveData

    private val initialArtistsListing: Listing<Artist> =
            repository.searchArtists(disposables = disposables)
    private var searchArtistsListing: Listing<Artist>? = null
    private var suggestionsLiveData: LiveData<List<Artist>>? = null

    private var retrySearchArtists: () -> Unit = initialArtistsListing.retry
    private var refreshSearchArtists: () -> Unit = initialArtistsListing.refresh

    val networkConnectivityBannerLiveData = MutableLiveData<Boolean>()

    init {
        artistsLiveData.addSource(initialArtistsListing.pagedList) {
            artistsLiveData.value = it
        }
        loadingStateLiveData.addSource(initialArtistsListing.loadingState) {
            loadingStateLiveData.value = it
        }
    }

    fun onCreate(networkConnectivityObservable: Observable<Boolean>?) {
        super.onCreate(networkConnectivityObservable) { isConnected ->
            networkConnectivityBannerLiveData.postValue(isConnected.not())
            if (isConnected) {
                retrySearchArtists.invoke()
            }
        }
    }

    fun onRetry() {
        retrySearchArtists.invoke()
    }

    fun onRefresh() {
        refreshSearchArtists.invoke()
    }

    fun onSearchQuerySubmit(query: String) {
        artistSuggestionsLiveData.postValue(null)
        searchArtistsListing = repository.searchArtists(query, disposables)
                .apply {
                    artistsLiveData.removeSource(initialArtistsListing.pagedList)
                    artistsLiveData.addSource(this.pagedList) {
                        artistsLiveData.value = it
                    }
                    loadingStateLiveData.removeSource(initialArtistsListing.loadingState)
                    loadingStateLiveData.addSource(this.loadingState) {
                        loadingStateLiveData.value = it
                    }
                    retrySearchArtists = this.retry
                    refreshSearchArtists = this.refresh
                }
    }

    fun onSearchQueryChange(query: String) {
        suggestionsLiveData?.let {
            artistSuggestionsLiveData.removeSource(it)
        }
        suggestionsLiveData = repository.getArtistSuggestions(query, disposables)
                .apply {
                    artistSuggestionsLiveData.addSource(this) {
                        artistSuggestionsLiveData.value = it
                    }
                }
    }

    fun onArtistClick(artist: Artist?) {
        artistDetailsLiveData.postValue(artist)
    }
}
