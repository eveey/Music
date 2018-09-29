package com.evastos.music.inject.module

import com.evastos.music.data.exception.ExceptionMappers
import com.evastos.music.data.exception.spotify.SpotifyExceptionMapper
import com.evastos.music.domain.exception.ExceptionMessageProviders
import com.evastos.music.domain.exception.spotify.SpotifyExceptionMessageProvider
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class ExceptionModule {

    @Binds
    abstract fun bindSpotifyExceptionMapper(
        spotifyExceptionMapper: SpotifyExceptionMapper
    ): ExceptionMappers.Spotify

    @Binds
    abstract fun bindSpotifyExceptionMessageProvider(
        spotifyExceptionMessageProvider: SpotifyExceptionMessageProvider
    ): ExceptionMessageProviders.Spotify
}
