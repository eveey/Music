package com.evastos.music.domain

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.evastos.music.data.model.spotify.item.artist.Artist
import com.evastos.music.data.model.spotify.user.User
import com.evastos.music.domain.livedata.Listing
import com.evastos.music.domain.livedata.SingleLiveEvent
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import io.reactivex.disposables.CompositeDisposable

interface Repositories {

    interface Spotify {

        interface Authentication {

            val authRequestLiveEvent: SingleLiveEvent<AuthenticationRequest>
            val authErrorLiveData: LiveData<String>
            val userLiveEvent: SingleLiveEvent<User>

            fun authenticateOrGetUser()

            fun handleAuthResponse(
                authResponse: AuthenticationResponse,
                disposables: CompositeDisposable
            )
        }

        interface Artists {

            val artistSearchLiveData: MutableLiveData<String>

            fun searchArtists(
                query: String? = null,
                disposables: CompositeDisposable
            ): Listing<Artist>

            fun getArtistSuggestions(
                query: String,
                disposables: CompositeDisposable
            ): LiveData<List<Artist>>
        }
    }
}
