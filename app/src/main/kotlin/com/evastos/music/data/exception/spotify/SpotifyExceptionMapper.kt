package com.evastos.music.data.exception.spotify

import com.evastos.music.data.exception.ExceptionMappers
import retrofit2.HttpException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class SpotifyExceptionMapper
@Inject constructor() : ExceptionMappers.Spotify {

    override fun map(throwable: Throwable): SpotifyException {
        var exception: SpotifyException = SpotifyException.UnknownException()
        if (throwable is SocketTimeoutException || throwable is UnknownHostException) {
            exception = SpotifyException.NetworkException()
        }
        if (throwable is ConnectException) {
            exception = SpotifyException.ServerException()
        }
        if (throwable is HttpException) {
            getExceptionFromResponse(throwable).let {
                exception = it
            }
        }
        if (throwable is SpotifyException.NetworkFailFastException) {
            exception = throwable
        }
        return exception
    }

    private fun getExceptionFromResponse(httpException: HttpException): SpotifyException {
        var exception: SpotifyException = SpotifyException.UnknownException()
        val statusCode = httpException.code()
        if (statusCode >= HttpURLConnection.HTTP_BAD_REQUEST
                && statusCode < HttpURLConnection.HTTP_INTERNAL_ERROR) {
            exception = SpotifyException.ClientException()
        } else if (statusCode >= HttpURLConnection.HTTP_INTERNAL_ERROR) {
            exception = SpotifyException.ServerException()
        }
        return exception
    }
}
