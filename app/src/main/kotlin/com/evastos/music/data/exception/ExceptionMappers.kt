package com.evastos.music.data.exception

import com.evastos.music.data.exception.spotify.SpotifyException

interface ExceptionMappers {
    interface Spotify : ExceptionMapper<SpotifyException>
}
