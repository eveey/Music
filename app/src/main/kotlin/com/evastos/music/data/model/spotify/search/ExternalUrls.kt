package com.evastos.music.data.model.spotify.search

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * External URLs.
 */
@JsonClass(generateAdapter = true)
data class ExternalUrls(
    @Json(name = "spotify") val spotify: String?
)
