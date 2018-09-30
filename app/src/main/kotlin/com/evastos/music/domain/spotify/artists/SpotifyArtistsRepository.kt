package com.evastos.music.domain.spotify.artists

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.content.Context
import android.support.annotation.MainThread
import com.evastos.music.data.exception.ExceptionMappers
import com.evastos.music.data.exception.spotify.SpotifyException
import com.evastos.music.data.model.spotify.item.ItemType
import com.evastos.music.data.model.spotify.item.ItemTypes
import com.evastos.music.data.model.spotify.item.artist.Artist
import com.evastos.music.data.persistence.db.artist.ArtistDao
import com.evastos.music.data.persistence.prefs.PreferenceStore
import com.evastos.music.data.rx.applySchedulers
import com.evastos.music.data.rx.checkNetwork
import com.evastos.music.data.rx.mapException
import com.evastos.music.data.service.spotify.SpotifyService
import com.evastos.music.domain.Repositories
import com.evastos.music.domain.datasource.spotify.artists.cache.ArtistsCacheDataSourceFactory
import com.evastos.music.domain.datasource.spotify.artists.search.ArtistsSearchDataSourceFactory
import com.evastos.music.domain.exception.ExceptionMessageProviders
import com.evastos.music.domain.livedata.Listing
import com.evastos.music.inject.qualifier.AppContext
import com.evastos.music.ui.util.extensions.formatQuery
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SpotifyArtistsRepository
@Inject constructor(
    @AppContext private val context: Context,
    private val service: SpotifyService,
    private val artistDao: ArtistDao,
    private val preferenceStore: PreferenceStore,
    private val exceptionMapper: ExceptionMappers.Spotify,
    private val exceptionMessageProvider: ExceptionMessageProviders.Spotify
) : Repositories.Spotify.Artists {

    companion object {
        private const val MARKET_FROM_TOKEN = "from_token"
        private const val PAGE_SIZE = 4
        private const val INITIAL_SEARCH = "ZHU"
    }

    override val artistSearchLiveData = MutableLiveData<String>()

    @MainThread
    override fun getCachedArtists(disposables: CompositeDisposable): LiveData<PagedList<Artist>> {
        artistSearchLiveData.postValue(preferenceStore.artistQuery)
        val sourceFactory = ArtistsCacheDataSourceFactory(
            artistDao,
            disposables
        )
        return LivePagedListBuilder(sourceFactory, PAGE_SIZE).build()
    }

    @MainThread
    override fun searchArtists(query: String?, disposables: CompositeDisposable): Listing<Artist> {
        var artistQuery = preferenceStore.artistQuery
        if (query != null) {
            artistQuery = query
        }
        if (artistQuery == null) {
            artistQuery = INITIAL_SEARCH
        }
        preferenceStore.artistQuery = artistQuery
        artistSearchLiveData.postValue(artistQuery)
        val sourceFactory = ArtistsSearchDataSourceFactory(
            context,
            artistQuery,
            service,
            artistDao,
            exceptionMapper,
            exceptionMessageProvider,
            disposables
        )
        return Listing(
            pagedList = LivePagedListBuilder(sourceFactory, PAGE_SIZE).build(),
            loadingState = Transformations.switchMap(sourceFactory.artistsSearchSourceLiveData) {
                it.loadingState
            },
            retry = {
                sourceFactory.artistsSearchSourceLiveData.value?.retryAllFailed()
            },
            refresh = {
                sourceFactory.artistsSearchSourceLiveData.value?.invalidate()
            }
        )
    }

    @MainThread
    override fun getArtistSuggestions(
        query: String,
        disposables: CompositeDisposable
    ): LiveData<List<Artist>> {
        val suggestionsLiveData = MutableLiveData<List<Artist>>()
        disposables.add(
            service.search(
                query = query.formatQuery(),
                types = ItemTypes().apply {
                    add(ItemType.ARTIST)
                },
                market = MARKET_FROM_TOKEN
            )
                    .checkNetwork(context, SpotifyException.NetworkFailFastException())
                    .mapException(exceptionMapper)
                    .applySchedulers()
                    .subscribe({ response ->
                        val artists = response.artists?.items ?: emptyList()
                        suggestionsLiveData.postValue(artists)
                    }, {
                        suggestionsLiveData.postValue(null)
                    }
                    )
        )
        return suggestionsLiveData
    }
}
