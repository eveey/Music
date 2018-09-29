package com.evastos.music.data.network.interceptor

import com.evastos.music.data.storage.prefs.PreferenceStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor
@Inject constructor(private val preferenceStore: PreferenceStore) : Interceptor {

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val BEARER_TOKEN = "Bearer %s"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().apply {
            header(HEADER_AUTHORIZATION, BEARER_TOKEN.format(preferenceStore.authData?.authToken))
        }.build()
        return chain.proceed(request)
    }
}
