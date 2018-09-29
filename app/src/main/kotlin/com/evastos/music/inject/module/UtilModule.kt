package com.evastos.music.inject.module

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
