package com.evastos.music.data.model.spotify

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "id") val id: String?,
    @Json(name = "display_name") val displayName: String?,
    @Json(name = "email") val email: String?,
    @Json(name = "type") val type: String?,
    @Json(name = "uri") val uri: String?
)
