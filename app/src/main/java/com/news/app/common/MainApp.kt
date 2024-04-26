package com.news.app.common

import android.app.Application
import com.news.core.App
import com.news.core.AppDependenciesProvider
import com.news.app.ui.di.common.AppComponent

class MainApp: Application(), App {

    private var appComponent: AppComponent? = null

    override fun provideAppDependenciesProvider(): AppDependenciesProvider {
        if (appComponent == null) appComponent = AppComponent.create(applicationContext)
        return appComponent!!
    }

}