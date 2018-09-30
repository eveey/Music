package com.evastos.music.inject.module

import android.content.Context
import com.evastos.music.data.persistence.db.MusicDatabase
import com.evastos.music.data.persistence.db.artist.ArtistDao
import com.evastos.music.data.persistence.prefs.PreferenceStore
import com.evastos.music.data.persistence.prefs.SharedPreferenceStore
import com.evastos.music.inject.qualifier.AppContext
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Suppress("unused")
@Module
class PersistenceModule {

    @Provides
    @Singleton
    fun providePreferenceStorage(@AppContext context: Context): PreferenceStore =
            SharedPreferenceStore(context)

    @Provides
    @Singleton
    fun provideDatabase(@AppContext context: Context): MusicDatabase =
            MusicDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideArtistDao(musicDatabase: MusicDatabase): ArtistDao =
            musicDatabase.artistDao()
}
