package com.evastos.music.inject.module

import android.content.Context
import com.evastos.music.data.storage.prefs.PreferenceStore
import com.evastos.music.data.storage.prefs.SharedPreferenceStore
import com.evastos.music.inject.qualifier.AppContext
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Suppress("unused")
@Module
class StorageModule {

    @Provides
    @Singleton
    fun providePreferenceStorage(@AppContext context: Context): PreferenceStore =
            SharedPreferenceStore(context)
}
