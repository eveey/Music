package com.evastos.music.data.persistence.prefs

import com.evastos.music.data.model.authentication.AuthData
import com.evastos.music.data.model.spotify.user.User

interface PreferenceStore {

    var authData: AuthData?
    var user: User?
    var artistQuery: String?

    object Constants {
        const val AUTH_DATA = "AUTH_DATA"
        const val USER = "USER"
        const val ARTIST_QUERY = "ARTIST_QUERY"
    }
}
