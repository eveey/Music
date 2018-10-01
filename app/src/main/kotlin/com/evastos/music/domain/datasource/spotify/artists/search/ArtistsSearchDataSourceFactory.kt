package com.evastos.music.domain.datasource.spotify.artists.search

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.evastos.music.data.exception.ExceptionMappers
import com.evastos.music.data.model.spotify.item.artist.Artist
import com.evastos.music.data.network.connectivity.NetworkConnectivityProvider
import com.evastos.music.data.service.spotify.SpotifyService
import com.evastos.music.domain.exception.ExceptionMessageProviders
import io.reactivex.disposables.CompositeDisposable

/**
 * A simple data source factory which also provides a way to observe the last created data source.
 * This allows us to channel its loading request status etc back to the UI. See the Listing creation
 * in the Repository class.
 */
class ArtistsSearchDataSourceFactory(
    private val query: String,
    private val spotifyService: SpotifyService,
    private val exceptionMapper: ExceptionMappers.Spotify,
    private val exceptionMessageProvider: ExceptionMessageProviders.Spotify,
    private val networkConnectivityProvider: NetworkConnectivityProvider,
    private val disposables: CompositeDisposable
) : DataSource.Factory<Int, Artist>() {

    val artistsSearchSourceLiveData = MutableLiveData<ArtistsSearchDataSource>()

    override fun create(): DataSource<Int, Artist> {
        val source = ArtistsSearchDataSource(
            query,
            spotifyService,
            exceptionMapper,
            exceptionMessageProvider,
            networkConnectivityProvider,
            disposables
        )
        artistsSearchSourceLiveData.postValue(source)
        return source
    }
}
