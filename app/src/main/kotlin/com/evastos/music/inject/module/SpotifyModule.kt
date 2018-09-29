package com.evastos.music.inject.module

import android.net.Uri
import com.evastos.music.BuildConfig
import com.evastos.music.data.service.spotify.SpotifyService
import com.evastos.music.data.service.spotify.scopes.Scopes
import com.evastos.music.data.service.spotify.scopes.SpotifyScopes
import com.evastos.music.inject.qualifier.SpotifyRedirectUri
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Suppress("unused")
@Module
class SpotifyModule {

    @Provides
    @Singleton
    fun provideRetrofit(okhttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl(BuildConfig.SPOTIFY_BASE_API_URL)
            client(okhttpClient)
            addConverterFactory(ScalarsConverterFactory.create())
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            addConverterFactory(MoshiConverterFactory.create(moshi))
        }.build()
    }

    @Provides
    @Singleton
    fun provideService(retrofit: Retrofit): SpotifyService {
        return retrofit.create(SpotifyService::class.java)
    }

    @Provides
    @Singleton
    fun provideScopes(): Scopes.Spotify {
        return SpotifyScopes()
    }

    @Provides
    @Singleton
    @SpotifyRedirectUri
    fun provideRedirectUri(): String {
        return Uri.Builder()
                .scheme(BuildConfig.SPOTIFY_REDIRECT_SCHEME)
                .authority(BuildConfig.SPOTIFY_REDIRECT_HOST)
                .build()
                .toString()
    }
}
