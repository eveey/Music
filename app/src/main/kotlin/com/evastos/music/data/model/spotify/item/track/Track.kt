package com.evastos.music.data.model.spotify.item.track

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Track Spotify item.
 */
@JsonClass(generateAdapter = true)
data class Track(
    @Json(name = "href") val href: String?
)
