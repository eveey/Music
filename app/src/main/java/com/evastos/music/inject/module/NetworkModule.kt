package com.evastos.music.inject.module

import com.evastos.music.BuildConfig
import com.evastos.music.data.network.interceptor.HeadersInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Suppress("unused")
@Module
class NetworkModule {

    companion object {
        private const val NETWORK_TIMEOUT_SECONDS = 30L
        private const val TIMBER_NETWORK_TAG = "api_call"
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message -> Timber.tag(TIMBER_NETWORK_TAG).d(message) }
                .apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    @Provides
    @Singleton
    fun provideOkHttp(
        loggingInterceptor: HttpLoggingInterceptor,
        headersInterceptor: HeadersInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
                .apply {
                    connectTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    readTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    writeTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    addInterceptor(headersInterceptor)
                    if (BuildConfig.DEBUG) {
                        addInterceptor(loggingInterceptor)
                    }
                }.build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()
}
