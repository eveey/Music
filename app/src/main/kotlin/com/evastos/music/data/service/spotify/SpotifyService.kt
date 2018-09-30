package com.evastos.music.data.service.spotify

import com.evastos.music.data.model.spotify.search.SearchResponse
import com.evastos.music.data.model.spotify.user.User
import com.evastos.music.data.model.spotify.item.ItemTypes
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface SpotifyService {

    @GET("v1/me")
    fun getUser(): Single<User>

    @GET("v1/search")
    fun search(
        @Query("q") query: String,
        @Query("type") types: ItemTypes,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): Single<SearchResponse>
}
