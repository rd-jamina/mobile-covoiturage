package com.example.app_covoiturage.di

import com.example.app_covoiturage.data.remote.osrm.OsrmApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OsrmModule {

    @Provides
    @Singleton
    fun provideOsrmApiService(): OsrmApiService {
        val client = OkHttpClient.Builder().build()
        return Retrofit.Builder()
            .baseUrl("https://router.project-osrm.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OsrmApiService::class.java)
    }
}