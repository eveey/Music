package com.evastos.music.domain.exception.spotify

import android.content.Context
import com.evastos.music.R
import com.evastos.music.data.exception.spotify.SpotifyException
import com.evastos.music.domain.exception.ExceptionMessageProviders
import com.evastos.music.inject.qualifier.AppContext
import javax.inject.Inject

class SpotifyExceptionMessageProvider
@Inject constructor(@AppContext private val context: Context) : ExceptionMessageProviders.Spotify {

    override fun getMessage(exception: SpotifyException): String? {
        return when (exception) {
            is SpotifyException.ClientException ->
                context.getString(R.string.error_client)
            is SpotifyException.ServerException ->
                context.getString(R.string.error_server_unavailable)
            is SpotifyException.NetworkException ->
                context.getString(R.string.error_network)
            is SpotifyException.NetworkFailFastException -> null
            else -> context.getString(R.string.error_general)
        }
    }

    override fun getMessage(throwable: Throwable): String? {
        if (throwable is SpotifyException) {
            return getMessage(throwable)
        }
        return context.getString(R.string.error_general)
    }

    override val authErrorMessage: String = context.getString(R.string.error_auth_spotify)
}
