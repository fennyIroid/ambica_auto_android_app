package com.ambica.auto.app.data.source.remote.interceptor

import com.ambica.auto.app.data.source.local.datastore.AppPreferenceDataStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class RequestInterceptor @Inject constructor(
    private val appPreferenceDataStore: AppPreferenceDataStore,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
        runBlocking {
            val token = appPreferenceDataStore.getUserTokenData()
            if (!token.isNullOrBlank()) {
                builder.addHeader("Authorization", "Bearer $token")
            }
        }
        return chain.proceed(builder.build())
    }
}
