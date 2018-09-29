package com.evastos.music.domain.exception

import com.evastos.music.data.exception.spotify.SpotifyException

interface ExceptionMessageProviders {

    interface Spotify : ExceptionMessageProvider<SpotifyException> {
        val authErrorMessage: String
    }
}
