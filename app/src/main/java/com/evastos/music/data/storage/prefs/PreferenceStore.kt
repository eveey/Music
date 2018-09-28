package com.evastos.music.data.storage.prefs

import com.evastos.music.data.model.spotify.User

interface PreferenceStore {

    var authToken: String?
    var authTokenExpiresIn: Int // seconds
    var authTokenRefreshedAt: Long // milliseconds
    var authCode: String?
    var user: User?

    object Constants {
        const val AUTH_TOKEN = "AUTH_TOKEN"
        const val AUTH_TOKEN_EXPIRES_IN = "AUTH_TOKEN_EXPIRES_IN"
        const val AUTH_TOKEN_REFRESHED_AT = "AUTH_TOKEN_REFRESHED_AT"
        const val AUTH_CODE = "AUTH_CODE"
        const val USER = "USER"
    }
}
