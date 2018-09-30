package com.evastos.music.inject.module

import com.evastos.music.ui.spotify.artists.ArtistsActivity
import com.evastos.music.ui.spotify.authentication.AuthenticationActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector
    internal abstract fun bindAuthenticationActivity(): AuthenticationActivity

    @ContributesAndroidInjector
    internal abstract fun bindArtistsActivity(): ArtistsActivity
}
