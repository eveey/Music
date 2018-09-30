package com.evastos.music.data.model.spotify.item.artist

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.evastos.music.data.model.spotify.item.ItemType
import com.evastos.music.data.model.spotify.search.ExternalUrls
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Artist Spotify item.
 */
@Entity(tableName = "artist")
@JsonClass(generateAdapter = true)
data class Artist(
    @PrimaryKey @ColumnInfo(name = "artistId")
    @Json(name = "id")
    val id: String,
    @ColumnInfo(name = "spotify_external_url")
    @Json(name = "external_urls")
    val externalUrls: ExternalUrls?,
    @ColumnInfo(name = "followers_count") @Json(name = "followers")
    val followers: Followers?,
    @ColumnInfo(name = "genres") @Json(name = "genres")
    val genres: List<String>?,
    @ColumnInfo(name = "href")
    @Json(name = "href")
    val href: String?,
    @ColumnInfo(name = "image_path")
    @Json(name = "images")
    val images: List<Image>?,
    @ColumnInfo(name = "name")
    @Json(name = "name")
    val name: String?,
    @ColumnInfo(name = "popularity")
    @Json(name = "popularity")
    val popularity: Int?,
    @ColumnInfo(name = "type")
    @Json(name = "type")
    val type: ItemType?,
    @ColumnInfo(name = "uri")
    @Json(name = "uri")
    val uri: String?
)
