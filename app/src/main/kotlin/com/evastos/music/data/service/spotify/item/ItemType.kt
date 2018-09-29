package com.evastos.music.data.service.spotify.item

import java.util.*

/**
 * Spotify search item types.
 */
enum class ItemType {
    ALBUM,
    ARTIST,
    PLAYLIST,
    TRACK;

    override fun toString(): String {
        return name.toLowerCase(Locale.US)
    }
}
