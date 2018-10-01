package com.evastos.music.data.model.spotify.item.artist

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

/**
 * Image Spotify item.
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class Image(
    @Json(name = "width") val width: Int?,
    @Json(name = "height") val height: Int?,
    @Json(name = "url") val url: String?
) : Parcelable
