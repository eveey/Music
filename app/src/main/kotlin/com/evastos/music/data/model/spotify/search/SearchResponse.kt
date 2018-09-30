package com.evastos.music.data.model.spotify.search

import com.evastos.music.data.model.spotify.item.Items
import com.evastos.music.data.model.spotify.item.album.Album
import com.evastos.music.data.model.spotify.item.artist.Artist
import com.evastos.music.data.model.spotify.item.playlist.Playlist
import com.evastos.music.data.model.spotify.item.track.Track
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResponse(
    @Json(name = "albums") val albums: Items<Album>?,
    @Json(name = "artists") val artists: Items<Artist>?,
    @Json(name = "playlists") val playlists: Items<Playlist>?,
    @Json(name = "tracks") val tracks: Items<Track>?
)
