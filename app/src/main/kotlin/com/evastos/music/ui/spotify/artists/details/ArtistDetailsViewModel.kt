package com.evastos.music.ui.spotify.artists.details

import android.arch.lifecycle.MutableLiveData
import com.evastos.music.data.model.spotify.item.artist.Artist
import com.evastos.music.ui.base.BaseViewModel
import io.reactivex.Observable
import javax.inject.Inject

class ArtistDetailsViewModel
@Inject constructor(
) : BaseViewModel() {

    val imageLiveData = MutableLiveData<String>()
    val nameLiveData = MutableLiveData<String>()
    val genresLiveData = MutableLiveData<String>()
    val followersLiveData = MutableLiveData<Int>()
    val externalUrlLiveData = MutableLiveData<String>()

    fun onCreate(artist: Artist, networkConnectivityObservable: Observable<Boolean>?) {
        super.onCreate(networkConnectivityObservable) { isConnected ->
            networkConnectivityBannerLiveData.postValue(isConnected.not())
        }
        imageLiveData.value = artist.images?.let { images ->
            if (images.isNotEmpty()) images[0].url else null
        }
        nameLiveData.value = artist.name
        genresLiveData.value = artist.genres?.joinToString(", ")
        followersLiveData.value = artist.followers?.total
        externalUrlLiveData.value = artist.externalUrls?.spotify
    }
}
