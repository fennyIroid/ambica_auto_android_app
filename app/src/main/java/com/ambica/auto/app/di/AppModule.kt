package com.ambica.auto.app.di

import android.content.Context
import com.ambica.auto.app.data.source.local.datastore.AppPreferenceDataStore
import com.ambica.auto.app.data.repository.JobServiceImpl
import com.ambica.auto.app.data.source.local.demo.DemoRepository
import com.ambica.auto.app.data.source.local.demo.DemoRepositoryImpl
import com.ambica.auto.app.data.source.local.session.SessionStore
import com.ambica.auto.app.data.source.local.session.SessionStoreImpl
import com.ambica.auto.app.domain.repository.JobService
import com.ambica.auto.app.data.source.remote.repository.ApiRepository
import com.ambica.auto.app.data.source.remote.repository.ApiRepositoryImpl
import com.ambica.auto.app.data.source.remote.repository.ApiServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideApiRepository(apiServices: ApiServices): ApiRepository {
        return ApiRepositoryImpl(apiServices)
    }

    @Provides
    @Singleton
    fun provideAppPreferenceDataStore(@ApplicationContext context: Context): AppPreferenceDataStore {
        return AppPreferenceDataStore(context)
    }

    @Provides
    @Singleton
    fun provideDemoRepository(): DemoRepository = DemoRepositoryImpl()

    @Provides
    @Singleton
    fun provideSessionStore(impl: SessionStoreImpl): SessionStore = impl

    @Provides
    @Singleton
    fun provideJobService(impl: JobServiceImpl): JobService = impl
}
