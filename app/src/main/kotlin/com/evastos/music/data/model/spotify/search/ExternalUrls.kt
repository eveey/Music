package com.evastos.music.data.model.spotify.search

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

/**
 * External URLs.
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class ExternalUrls(
    @Json(name = "spotify") val spotify: String?
) : Parcelable
