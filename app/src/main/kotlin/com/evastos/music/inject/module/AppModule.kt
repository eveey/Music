package com.evastos.music.inject.module

import android.content.Context
import com.evastos.music.MusicApp
import com.evastos.music.inject.qualifier.AppContext
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    @AppContext
    fun provideAppContext(): Context = MusicApp.instance
}
