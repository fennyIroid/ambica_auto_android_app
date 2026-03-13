package com.ambica.auto.app.data.source.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == 403) {
            // Optional: AmbicaAutoApp.instance?.restartApp()
        }
        return response
    }
}
