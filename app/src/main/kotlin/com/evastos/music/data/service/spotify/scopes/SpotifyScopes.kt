package com.evastos.music.data.service.spotify.scopes

class SpotifyScopes : Scopes.Spotify {

    companion object {
        private const val SCOPE_APP_REMOTE_CONTROL = "app-remote-control"
        private const val SCOPE_USER_READ_EMAIL = "user-read-email"
        private const val SCOPE_USER_READ_PRIVATE = "user-read-private"
    }

    override fun getScopes(): Array<String> {
        return arrayOf(SCOPE_APP_REMOTE_CONTROL, SCOPE_USER_READ_EMAIL, SCOPE_USER_READ_PRIVATE)
    }
}
