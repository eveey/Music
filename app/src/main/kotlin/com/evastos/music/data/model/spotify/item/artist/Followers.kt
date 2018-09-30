package com.evastos.music.data.model.spotify.item.artist

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Followers Spotify item.
 */
@JsonClass(generateAdapter = true)
data class Followers(
    @Json(name = "total") val total: Int?
)
