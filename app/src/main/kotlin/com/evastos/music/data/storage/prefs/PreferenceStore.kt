package com.evastos.music.data.storage.prefs

import com.evastos.music.data.model.spotify.user.User
import com.evastos.music.domain.model.AuthData

interface PreferenceStore {

    var authData: AuthData?
    var user: User?

    object Constants {
        const val AUTH_DATA = "AUTH_DATA"
        const val USER = "USER"
    }
}
