package com.evastos.music.inject.module

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.evastos.music.inject.qualifier.ViewModelKey
import com.evastos.music.inject.viewmodel.ViewModelFactory
import com.evastos.music.ui.spotify.artists.ArtistsViewModel
import com.evastos.music.ui.spotify.artists.details.ArtistDetailsViewModel
import com.evastos.music.ui.spotify.authentication.AuthenticationViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthenticationViewModel::class)
    abstract fun bindAuthenticationViewModel(
        authenticationViewModel: AuthenticationViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ArtistsViewModel::class)
    abstract fun bindArtistsViewModel(
        artistsViewModel: ArtistsViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ArtistDetailsViewModel::class)
    abstract fun bindArtistDetailsViewModel(
        artistsDetailsViewModel: ArtistDetailsViewModel
    ): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
