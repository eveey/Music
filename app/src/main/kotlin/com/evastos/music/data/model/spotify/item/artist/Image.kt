package com.evastos.music.data.model.spotify.item.artist

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Image Spotify item.
 */
@JsonClass(generateAdapter = true)
data class Image(
    @Json(name = "width") val width: Int?,
    @Json(name = "height") val height: Int?,
    @Json(name = "url") val url: String?
)
