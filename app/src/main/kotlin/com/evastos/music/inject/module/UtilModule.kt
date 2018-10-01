package com.evastos.music.inject.module

import android.content.Context
import com.evastos.music.data.network.connectivity.NetworkConnectivityProvider
import com.evastos.music.inject.qualifier.AppContext
import com.evastos.music.ui.util.DateTimeUtil
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Suppress("unused")
@Module
class UtilModule {

    @Provides
    @Singleton
    fun provideDateTimeUtil(): DateTimeUtil {
        return DateTimeUtil()
    }
}
