package com.evastos.music.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthData(
    @Json(name = "authToken") val authToken: String,
    @Json(name = "authTokenExpiresIn") val authTokenExpiresIn: Long, // millis
    @Json(name = "authTokenRefreshedAt") val authTokenRefreshedAt: Long // millis
)
