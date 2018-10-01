package com.evastos.music.inject.module

import com.evastos.music.domain.Repositories
import com.evastos.music.domain.spotify.artists.ArtistsRepository
import com.evastos.music.domain.spotify.authentication.AuthenticationRepository
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindSpotifyAuthenticationRepository(
        authenticationRepository: AuthenticationRepository
    ): Repositories.Spotify.Authentication

    @Binds
    abstract fun bindSpotifyArtistsRepository(
        artistsRepository: ArtistsRepository
    ): Repositories.Spotify.Artists
}
