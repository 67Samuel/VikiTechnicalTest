package com.samuel.vikitechnicaltest.di

import com.samuel.vikitechnicaltest.business.data.repository.RepositoryImpl
import com.samuel.vikitechnicaltest.business.domain.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRepository(
        repositoryImpl: RepositoryImpl
    ): Repository
}