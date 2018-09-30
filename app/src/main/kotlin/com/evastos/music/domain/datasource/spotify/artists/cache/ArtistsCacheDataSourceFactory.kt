package com.evastos.music.domain.datasource.spotify.artists.cache

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.evastos.music.data.model.spotify.item.artist.Artist
import com.evastos.music.data.persistence.db.artist.ArtistDao
import io.reactivex.disposables.CompositeDisposable

/**
 * A simple data source factory which also provides a way to observe the last created data source.
 */
class ArtistsCacheDataSourceFactory(
    private val artistDao: ArtistDao,
    private val disposables: CompositeDisposable
) : DataSource.Factory<Int, Artist>() {

    val artistsCacheSourceLiveData = MutableLiveData<ArtistsCacheDataSource>()

    override fun create(): DataSource<Int, Artist> {
        val source = ArtistsCacheDataSource(
            artistDao,
            disposables
        )
        artistsCacheSourceLiveData.postValue(source)
        return source
    }
}
