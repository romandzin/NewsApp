package com.news.app.ui.di.common

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface ApplicationModule {

    @Binds
    @Singleton
    fun providesApplicationContext(application: Application): Context

}