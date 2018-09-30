package com.evastos.music.data.model.spotify.item.playlist

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Playlist Spotify item.
 */
@JsonClass(generateAdapter = true)
data class Playlist(
    @Json(name = "href") val href: String?
)
