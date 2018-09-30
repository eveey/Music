package com.evastos.music.domain.datasource.spotify.artists.search

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import android.content.Context
import com.evastos.music.data.exception.ExceptionMappers
import com.evastos.music.data.exception.spotify.SpotifyException
import com.evastos.music.data.model.spotify.item.ItemType
import com.evastos.music.data.model.spotify.item.ItemTypes
import com.evastos.music.data.model.spotify.item.artist.Artist
import com.evastos.music.data.model.spotify.search.SearchResponse
import com.evastos.music.data.persistence.db.artist.ArtistDao
import com.evastos.music.data.rx.applySchedulers
import com.evastos.music.data.rx.checkNetwork
import com.evastos.music.data.rx.delayError
import com.evastos.music.data.rx.mapException
import com.evastos.music.data.service.spotify.SpotifyService
import com.evastos.music.domain.exception.ExceptionMessageProviders
import com.evastos.music.domain.livedata.LoadingState
import com.evastos.music.inject.qualifier.AppContext
import com.evastos.music.ui.util.extensions.formatQuery
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

class ArtistsSearchDataSource(
    @AppContext private val context: Context,
    private val query: String,
    private val spotifyService: SpotifyService,
    private val artistDao: ArtistDao,
    private val exceptionMapper: ExceptionMappers.Spotify,
    private val exceptionMessageProvider: ExceptionMessageProviders.Spotify,
    private val disposables: CompositeDisposable
) : PageKeyedDataSource<Int, Artist>() {

    companion object {
        private const val MARKET_FROM_TOKEN = "from_token"
        private const val PAGE_INITIAL = 0
        private const val PAGE_SIZE = 20
    }

    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null

    val loadingState = MutableLiveData<LoadingState>()

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.invoke()
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, Artist>
    ) {
        // ignored, since we only ever append to our initial load
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Artist>) {
        disposables.add(
            searchArtists(params.key)
                    .subscribe({ response ->
                        val artists = response.artists?.items ?: emptyList()
                        retry = null
                        loadingState.postValue(LoadingState.Success())
                        callback.onResult(artists, getNextPage(params.key, response))
                    }, {
                        retry = {
                            loadAfter(params, callback)
                        }
                        loadingState.postValue(
                            LoadingState.Error(exceptionMessageProvider.getMessage(it))
                        )
                    }
                    )
        )
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Artist>
    ) {
        disposables.add(
            searchArtists(PAGE_INITIAL)
                    .subscribe({ response ->
                        val nextPage = getNextPage(PAGE_INITIAL, response)
                        val previousPage = PAGE_INITIAL.minus(1)
                        val artists = response.artists?.items ?: emptyList()
                        retry = null
                        loadingState.postValue(LoadingState.Success())
                        callback.onResult(artists, previousPage, nextPage)
                    }, {
                        retry = {
                            loadInitial(params, callback)
                        }
                        loadingState.postValue(
                            LoadingState.Error(exceptionMessageProvider.getMessage(it))
                        )
                    })
        )
    }

    private fun searchArtists(page: Int): Single<SearchResponse> =
            spotifyService.search(
                query = query.formatQuery(),
                types = ItemTypes().apply {
                    add(ItemType.ARTIST)
                },
                market = MARKET_FROM_TOKEN,
                limit = PAGE_SIZE,
                offset = page * PAGE_SIZE
            )
                    .doOnSubscribe {
                        loadingState.postValue(LoadingState.Loading())
                    }
                    .checkNetwork(context, SpotifyException.NetworkFailFastException())
                    .doOnSuccess { response ->
                        if (page == PAGE_INITIAL) {
                            artistDao.deleteAllArtists()
                        }
                        response.artists?.items?.let {
                            artistDao.insertArtists(it)
                        }
                    }
                    .delayError()
                    .mapException(exceptionMapper)
                    .applySchedulers()

    private fun getNextPage(page: Int, response: SearchResponse): Int? {
        val nextPageVal = page.plus(1)
        val totalPagesVal = (response.artists?.total ?: 0) / PAGE_SIZE + 1
        return if (nextPageVal <= totalPagesVal) {
            nextPageVal
        } else {
            null
        }
    }
}
