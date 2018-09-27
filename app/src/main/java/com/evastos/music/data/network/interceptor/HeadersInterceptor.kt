package com.evastos.music.data.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeadersInterceptor @Inject constructor() : Interceptor {

    companion object {
        private const val HEADER_ACCEPT = "Accept"
        private const val ACCEPT_JSON = "application/json"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().apply {
            header(HEADER_ACCEPT, ACCEPT_JSON)
        }.build()
        return chain.proceed(request)
    }
}
