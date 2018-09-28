package com.evastos.music.inject.module

import com.evastos.music.domain.Repositories
import com.evastos.music.domain.spotify.authentication.SpotifyAuthenticationRepository
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindSpotifyAuthenticationRepository(
        spotifyAuthenticationRepository: SpotifyAuthenticationRepository
    ): Repositories.Spotify.Authentication
}
