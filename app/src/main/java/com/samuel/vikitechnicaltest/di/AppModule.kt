package com.samuel.vikitechnicaltest.di

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.samuel.vikitechnicaltest.business.data.datastore.AppDataStore
import com.samuel.vikitechnicaltest.business.data.datastore.AppDataStoreManager
import com.samuel.vikitechnicaltest.business.data.network.Api
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesApi() : Api {
        return Retrofit.Builder()
            .baseUrl("https://v6.exchangerate-api.com/v6/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }

    @Singleton
    @Provides
    fun provideDataStoreManager(
        application: Application,
    ): AppDataStore {
        return AppDataStoreManager(application)
    }

    @Singleton
    @Provides
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
}