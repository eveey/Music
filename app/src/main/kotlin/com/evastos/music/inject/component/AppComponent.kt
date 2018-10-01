package com.evastos.music.inject.component

import com.evastos.music.MusicApp
import com.evastos.music.inject.module.ActivityBuilder
import com.evastos.music.inject.module.AppModule
import com.evastos.music.inject.module.ExceptionModule
import com.evastos.music.inject.module.NetworkModule
import com.evastos.music.inject.module.PersistenceModule
import com.evastos.music.inject.module.ReceiverBuilder
import com.evastos.music.inject.module.RepositoryModule
import com.evastos.music.inject.module.SpotifyModule
import com.evastos.music.inject.module.UtilModule
import com.evastos.music.inject.module.ViewModelModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Suppress("unused")
@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ActivityBuilder::class,
    ReceiverBuilder::class,
    AppModule::class,
    SpotifyModule::class,
    RepositoryModule::class,
    PersistenceModule::class,
    NetworkModule::class,
    ExceptionModule::class,
    UtilModule::class,
    ViewModelModule::class
])
interface AppComponent : AndroidInjector<MusicApp>
