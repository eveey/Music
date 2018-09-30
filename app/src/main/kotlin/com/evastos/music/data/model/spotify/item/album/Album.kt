package com.evastos.music.data.model.spotify.item.album

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Album Spotify item.
 */
@JsonClass(generateAdapter = true)
data class Album(
    @Json(name = "href") val href: String?
)
