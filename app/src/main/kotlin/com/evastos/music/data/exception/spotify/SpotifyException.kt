package com.evastos.music.data.exception.spotify

sealed class SpotifyException : Throwable() {

    class ClientException : SpotifyException()

    class ServerException : SpotifyException()

    class NetworkException : SpotifyException()

    class UnknownException : SpotifyException()
}
