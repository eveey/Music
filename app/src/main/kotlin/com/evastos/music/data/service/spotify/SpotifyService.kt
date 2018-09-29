package com.evastos.music.data.service.spotify

import com.evastos.music.data.model.spotify.User
import io.reactivex.Single
import retrofit2.http.GET

interface SpotifyService {

    @GET("v1/me")
    fun getUser(): Single<User>
}
