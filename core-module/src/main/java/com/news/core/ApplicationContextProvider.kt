package com.news.core

import android.content.Context
import javax.inject.Named

interface ApplicationContextProvider {

    @Named("application.context")
    fun provideApplicationContext(): Context

}