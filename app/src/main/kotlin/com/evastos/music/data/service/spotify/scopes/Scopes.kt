package com.evastos.music.data.service.spotify.scopes

interface Scopes {
    interface Spotify {
        fun getScopes(): Array<String>
    }
}
