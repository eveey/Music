package com.evastos.music.domain.spotify.artists

import com.evastos.music.data.exception.ExceptionMappers
import com.evastos.music.data.service.spotify.SpotifyService
import com.evastos.music.domain.Repositories
import com.evastos.music.domain.exception.ExceptionMessageProviders
import javax.inject.Inject

class SpotifyArtistsRepository
@Inject constructor(
    private val spotifyService: SpotifyService,
    private val spotifyExceptionMapper: ExceptionMappers.Spotify,
    private val spotifyExceptionMessageProvider: ExceptionMessageProviders.Spotify
) : Repositories.Spotify.Artists