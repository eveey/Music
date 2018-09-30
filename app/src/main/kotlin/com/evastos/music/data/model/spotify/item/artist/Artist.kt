package com.evastos.music.data.model.spotify.item.artist

import com.evastos.music.data.model.spotify.item.ItemType
import com.evastos.music.data.model.spotify.search.ExternalUrls
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Artist Spotify item.
 */
@JsonClass(generateAdapter = true)
data class Artist(
    @Json(name = "id") val id: String?,
    @Json(name = "external_urls") val externalUrls: ExternalUrls?,
    @Json(name = "followers") val followers: Followers?,
    @Json(name = "genres") val genres: List<String>?,
    @Json(name = "href") val href: String?,
    @Json(name = "images") val images: List<Image>?,
    @Json(name = "name") val name: String?,
    @Json(name = "popularity") val popularity: Int?,
    @Json(name = "type") val type: ItemType?,
    @Json(name = "uri") val uri: String?
)
