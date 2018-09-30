package com.evastos.music.domain.datasource.spotify.artists.cache

import android.arch.paging.PageKeyedDataSource
import com.evastos.music.data.model.spotify.item.artist.Artist
import com.evastos.music.data.persistence.db.artist.ArtistDao
import com.evastos.music.data.rx.applySchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class ArtistsCacheDataSource(
    private val artistDao: ArtistDao,
    private val disposables: CompositeDisposable
) : PageKeyedDataSource<Int, Artist>() {

    companion object {
        private const val PAGE_INITIAL = 0
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, Artist>
    ) {
        // ignored, since we only ever append to our initial load
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Artist>) {
        // ignored, since we only ever append to our initial load
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Artist>
    ) {
        disposables.add(
            artistDao.getArtists()
                    .applySchedulers()
                    .subscribe({
                        if (it.isNotEmpty()) callback.onResult(it, PAGE_INITIAL, PAGE_INITIAL)
                    }, { Timber.e(it) })
        )
    }
}
