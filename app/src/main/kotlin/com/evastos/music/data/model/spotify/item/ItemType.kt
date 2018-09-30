package com.evastos.music.data.model.spotify.item

import com.squareup.moshi.Json
import java.util.*

/**
 * Spotify search item types.
 */
enum class ItemType {
    @Json(name = "album")
    ALBUM,
    @Json(name = "artist")
    ARTIST,
    @Json(name = "playlist")
    PLAYLIST,
    @Json(name = "track")
    TRACK;

    override fun toString(): String {
        return name.toLowerCase(Locale.US)
    }
}
