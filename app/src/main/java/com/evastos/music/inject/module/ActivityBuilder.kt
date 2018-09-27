package com.evastos.music.inject.module

import com.evastos.music.ui.authentication.AuthenticationActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector
    internal abstract fun bindAuthenticationActivity(): AuthenticationActivity
}
