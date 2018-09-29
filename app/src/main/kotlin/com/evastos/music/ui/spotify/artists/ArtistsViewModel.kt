package com.evastos.music.ui.spotify.artists

import android.arch.lifecycle.MutableLiveData
import com.evastos.music.data.rx.applySchedulers
import com.evastos.music.data.service.spotify.SpotifyService
import com.evastos.music.data.service.spotify.item.ItemType
import com.evastos.music.data.service.spotify.item.ItemTypes
import com.evastos.music.ui.base.BaseViewModel
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject

class ArtistsViewModel
@Inject constructor(
    private val spotifyService: SpotifyService
) : BaseViewModel() {

    val networkConnectivityBannerLiveData = MutableLiveData<Boolean>()

    fun onCreate(networkConnectivityObservable: Observable<Boolean>?) {
        super.onCreate(networkConnectivityObservable) { isConnected ->
            networkConnectivityBannerLiveData.postValue(isConnected.not())
        }

        disposables.add(spotifyService.search(
            query = "zhu*",
            types = ItemTypes().apply {
                add(ItemType.ARTIST)
            },
            limit = 20,
            offset = 0
        )
                .applySchedulers()
                .subscribe({
                    Timber.i(it)
                }, {
                    Timber.e(it)
                }))

    }
}
