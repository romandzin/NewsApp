package com.news.app.ui.di.common

import com.news.dat.data_impl.RepositoryImpl
import com.news.data.data_api.Repository
import dagger.Binds
import dagger.Module

@Module
interface RepositoryModule {

    @Binds
    fun bindRepository(repositoryImpl: RepositoryImpl): Repository
}