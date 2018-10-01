package com.evastos.music.inject.module

import android.content.Context
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
    fun providePreferenceStore(@AppContext context: Context): PreferenceStore =
            SharedPreferenceStore(context)
}
