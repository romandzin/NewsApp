package com.news.app.ui.di.common

import com.news.app.data.RepositoryImpl
import com.news.app.domain.Repository
import dagger.Binds
import dagger.Module

@Module
interface RepositoryModule {

    @Binds
    fun bindRepository(repositoryImpl: RepositoryImpl): Repository
}