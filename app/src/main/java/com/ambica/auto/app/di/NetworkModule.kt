package com.ambica.auto.app.di

import android.util.Log
import com.ambica.auto.app.data.source.local.datastore.AppPreferenceDataStore
import com.ambica.auto.app.data.source.remote.EndPoints
import com.ambica.auto.app.data.source.remote.interceptor.AuthInterceptor
import com.ambica.auto.app.data.source.remote.interceptor.RequestInterceptor
import com.ambica.auto.app.data.source.remote.repository.ApiServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    private val httpLoggingInterceptor =
        HttpLoggingInterceptor { message -> Log.d("Retrofit", message) }
            .setLevel(if (com.ambica.auto.app.BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)

    @Provides
    @Singleton
    fun provideRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(EndPoints.URLs.BASE_URL)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(appPreferenceDataStore: AppPreferenceDataStore): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(AuthInterceptor())
            .addInterceptor(RequestInterceptor(appPreferenceDataStore))
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiServices(retrofitBuilder: Retrofit.Builder, okHttpClient: OkHttpClient): ApiServices {
        return retrofitBuilder.client(okHttpClient).build().create(ApiServices::class.java)
    }
}
