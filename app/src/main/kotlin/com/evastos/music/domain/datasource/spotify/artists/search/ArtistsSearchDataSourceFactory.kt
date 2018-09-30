package com.evastos.music.domain.datasource.spotify.artists.search

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import android.content.Context
import com.evastos.music.data.exception.ExceptionMappers
import com.evastos.music.data.model.spotify.item.artist.Artist
import com.evastos.music.data.persistence.db.artist.ArtistDao
import com.evastos.music.data.service.spotify.SpotifyService
import com.evastos.music.domain.exception.ExceptionMessageProviders
import com.evastos.music.inject.qualifier.AppContext
import io.reactivex.disposables.CompositeDisposable

/**
 * A simple data source factory which also provides a way to observe the last created data source.
 * This allows us to channel its loading request status etc back to the UI. See the Listing creation
 * in the Repository class.
 */
class ArtistsSearchDataSourceFactory(
    @AppContext private val context: Context,
    private val query: String,
    private val spotifyService: SpotifyService,
    private val artistDao: ArtistDao,
    private val exceptionMapper: ExceptionMappers.Spotify,
    private val exceptionMessageProvider: ExceptionMessageProviders.Spotify,
    private val disposables: CompositeDisposable
) : DataSource.Factory<Int, Artist>() {

    val artistsSearchSourceLiveData = MutableLiveData<ArtistsSearchDataSource>()

    override fun create(): DataSource<Int, Artist> {
        val source = ArtistsSearchDataSource(
            context,
            query,
            spotifyService,
            artistDao,
            exceptionMapper,
            exceptionMessageProvider,
            disposables
        )
        artistsSearchSourceLiveData.postValue(source)
        return source
    }
}
