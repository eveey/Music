package com.evastos.music.data.model.spotify.item.artist

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

/**
 * Followers Spotify item.
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class Followers(
    @Json(name = "total") val total: Int?
) : Parcelable
